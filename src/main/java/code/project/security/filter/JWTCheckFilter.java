package code.project.security.filter;

import code.project.dto.JUserDTO;
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

    /**
     * MERGE NOTE:
     * - /api/account/** (아이디 찾기) /api/password/** (비번 찾기/변경) 추가
     * - /project/register, /project/user/**, /project/hospital/**, /project/pharmacy/**, /project/facility/**, /error 포함
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 로그인 필요 없는 공개 경로 (필터 자체를 스킵)
        if (path.startsWith("/api/account/") ||     // 계정찾기: send-code/verify-code/username
                path.startsWith("/api/password/") ||    // 비밀번호 찾기/변경
                path.equals("/project/register") ||     // 회원가입
                path.startsWith("/project/user/") ||    // 로그인/로그아웃 등 (기존 유지)
                path.startsWith("/project/hospital/") ||// 병원 공개 API
                path.startsWith("/project/pharmacy/") ||// 약국 공개 API
                path.startsWith("/project/facility/") ||// 시설 공개 API
                path.startsWith("/error")) {            // 에러 페이지
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JWTCheckFilter.doFilterInternal - path: {}", request.getRequestURI());

        // MERGE NOTE: 추가 예외 경로(기존 동작 유지)
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/project/register") || requestURI.equals("/project/user/modify")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization 헤더가 없거나 'Bearer '로 시작하지 않으면 그대로 통과 (기존 의도 유지)
        String authHeaderStr = request.getHeader("Authorization");
        if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.info("JWT claims: {}", claims);

            String username = (String) claims.get("username");
            String password = (String) claims.get("password");
            String name     = (String) claims.get("name");
            String address  = (String) claims.get("address");
            Integer age     = (Integer) claims.get("age");
            String email    = (String) claims.get("email");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            JUserDTO principal = new JUserDTO(username, password, name, address, age, email, roleNames);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 정상 통과
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.info("JWT validation error: {}", e.getMessage());
            // JSON 에러 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String msg = new Gson().toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            try (PrintWriter out = response.getWriter()) {
                out.println(msg);
            }
        }
    }
}
