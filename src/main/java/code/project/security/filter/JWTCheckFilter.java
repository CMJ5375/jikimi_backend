package code.project.security.filter;

import code.project.dto.JUserDTO;
import code.project.util.CustomJWTException;
import code.project.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1) CORS Preflight는 무조건 패스
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // 2) 로그인 불필요 공개 경로 (필터 자체 스킵) — 기존 경로 유지
        if (path.startsWith("/api/account/") ||     // 아이디 찾기
                path.startsWith("/api/password/") ||    // 비밀번호 찾기/변경
                path.equals("/project/register") ||
                path.equals("/project/user/login") ||
                path.equals("/project/user/logout") ||
                path.equals("/project/user/refresh") ||
                path.startsWith("/project/user/kakao") ||
                path.startsWith("/project/hospital/") ||
                path.startsWith("/project/pharmacy/") ||
                path.startsWith("/project/facility/") ||
                path.startsWith("/error")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeaderStr = request.getHeader("Authorization");

        // Authorization 헤더 없거나 Bearer 아니면 그대로 통과 (인가 규칙에서 후단 처리)
        if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.debug("JWT claims: {}", claims);

            // username 없으면 sub로 폴백
            String username = safeString(claims.get("username"));
            if (username.isBlank()) username = safeString(claims.get("sub"));

            // id(optional)
            Long userId = null;
            Object uid = claims.get("userId");
            if (uid instanceof Number n) userId = n.longValue();

            // 토큰에서 password는 절대 쓰지 않음
            String password = "N/A";

            String name         = safeString(claims.get("name"));
            String address      = safeString(claims.get("address"));
            String email        = safeString(claims.get("email"));
            String profileImage = safeString(claims.get("profileImage"));

            // age 안전 캐스팅
            Integer age = null;
            Object ageObj = claims.get("age");
            if (ageObj instanceof Number n) age = n.intValue();

            // ✅ roleNames 선호, 없으면 roles/authorities(ROLE_*)에서 복구
            List<String> roleNames = extractRoleNamesFlexible(claims);

            // 이미 인증돼 있지 않을 때만 세팅
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Principal 구성 (id 포함 생성자 사용)
                JUserDTO principal = new JUserDTO(
                        userId, username, password, name, address, age, email, profileImage, roleNames
                );

                // 권한 계산: ROLE_ 접두어 보정 후 SimpleGrantedAuthority 생성
                List<GrantedAuthority> computedAuthorities = roleNames.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .distinct()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // 최종 권한 선택 (토큰 기반 우선, 비어있으면 DTO 기본 로직)
                Collection<? extends GrantedAuthority> finalAuthorities =
                        computedAuthorities.isEmpty() ? principal.getAuthorities() : computedAuthorities;

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, password, finalAuthorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (CustomJWTException e) { // JWT 관련 예외만 잡기
            log.info("JWT validation error: {}", e.getMessage());
            handleJwtError(response);
        } catch (Exception e) { // 나머지 예외는 그대로 전달
            log.error("Non-JWT exception in JWTCheckFilter: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ===== Helpers =====

    private static String safeString(Object obj) {
        return (obj == null) ? "" : String.valueOf(obj);
    }

    /**
     * roleNames 우선 추출, 없으면 roles/authorities(ROLE_*)에서도 복구.
     * - roleNames: List 또는 CSV 모두 허용
     * - roles/authorities: ROLE_* 접두사를 제거하여 원형(ADMIN)으로 변환
     */
    @SuppressWarnings("unchecked")
    private static List<String> extractRoleNamesFlexible(Map<String, Object> claims) {
        // 1) roleNames 우선
        List<String> roleNames = extractRoleNames(claims.get("roleNames"));
        if (!roleNames.isEmpty()) return roleNames;

        // 2) roles (대개 ROLE_* 문자열 배열)
        List<String> fromRoles = extractAsList(claims.get("roles")).stream()
                .map(s -> s.startsWith("ROLE_") ? s.substring(5) : s)
                .toList();
        if (!fromRoles.isEmpty()) return fromRoles;

        // 3) authorities (대개 ROLE_* 문자열 배열)
        List<String> fromAuthorities = extractAsList(claims.get("authorities")).stream()
                .map(s -> s.startsWith("ROLE_") ? s.substring(5) : s)
                .toList();
        if (!fromAuthorities.isEmpty()) return fromAuthorities;

        return Collections.emptyList();
    }

    /**
     * roleNames 클레임을 안전하게 파싱:
     * - List<?> 형태면 문자열 리스트로 변환
     * - String(CSV) 형태면 콤마로 분리
     * - 그 외/누락이면 빈 리스트
     */
    @SuppressWarnings("unchecked")
    private static List<String> extractRoleNames(Object rolesObj) {
        if (rolesObj == null) return Collections.emptyList();

        if (rolesObj instanceof List<?> rawList) {
            return rawList.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        if (rolesObj instanceof String s) {
            if (s.isBlank()) return Collections.emptyList();
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        }

        // 예상 외 타입은 무시
        return Collections.emptyList();
    }

    /** 임의 오브젝트를 문자열 리스트로 치환 (List<String> 또는 CSV 모두 수용) */
    @SuppressWarnings("unchecked")
    private static List<String> extractAsList(Object obj) {
        if (obj == null) return Collections.emptyList();
        if (obj instanceof List<?> list) {
            return list.stream().filter(Objects::nonNull).map(Object::toString).toList();
        }
        if (obj instanceof String s) {
            if (s.isBlank()) return Collections.emptyList();
            return Arrays.stream(s.split(",")).map(String::trim).filter(t -> !t.isEmpty()).toList();
        }
        return Collections.emptyList();
    }

    // 응답 헬퍼
    private void handleJwtError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String msg = new Gson().toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
        try (PrintWriter out = response.getWriter()) {
            out.println(msg);
        }
    }
}
