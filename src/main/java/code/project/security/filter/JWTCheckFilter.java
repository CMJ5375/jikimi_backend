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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1) CORS Preflight는 무조건 패스
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // 2) 로그인 불필요 공개 경로 (필터 자체 스킵)
        if (path.startsWith("/api/account/") ||     // 아이디 찾기
            path.startsWith("/api/password/") ||    // 비밀번호 찾기/변경
            path.equals("/project/register") ||     // 회원가입
            path.startsWith("/project/user/") ||    // 로그인/로그아웃 등
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

            String username = (String) claims.get("username");
            String password = (String) claims.get("password");
            String name     = (String) claims.get("name");
            String address  = (String) claims.get("address");
            String email    = (String) claims.get("email");
            @SuppressWarnings("unchecked")
            List<String> roleNames = (List<String>) claims.get("roleNames");

            // age 안전 캐스팅
            Integer age = null;
            Object ageObj = claims.get("age");
            if (ageObj instanceof Number) {
                age = ((Number) ageObj).intValue();
            }

            // 이미 인증돼 있지 않을 때만 세팅
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                JUserDTO principal = new JUserDTO(username, password, name, address, age, email, roleNames);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        }
        // JWT 관련 예외만 잡기
        catch (CustomJWTException e) {
            log.info("JWT validation error: {}", e.getMessage());
            handleJwtError(response);
        }
        // 나머지 예외는 JWT 에러로 오인하지 않고 그대로 던지기
        catch (Exception e) {
            log.error("Non-JWT exception in JWTCheckFilter: {}", e.getMessage(), e);
            throw e; // 그대로 컨트롤러/전역예외로 전달
        }
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
