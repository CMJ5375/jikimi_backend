package code.project.security.filter;

import code.project.dto.UserDTO;
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

        // 로그인, 회원가입, 그리고 병원/약국 정보 API는 필터 건너뛰기
        if(path.startsWith("/project/user/") || path.startsWith("/api/facilities")) {
            return true;
        }

        log.info("체크url {}", path);
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal : 검증중^^");

        String authHeaderStr = request.getHeader("Authorization");

        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("JWT claims" + claims);

            String username = (String) claims.get("username");
            String password = (String) claims.get("password");
            String name = (String) claims.get("name");
            String address = (String) claims.get("address");
            Integer age = (Integer) claims.get("age");
            String email = (String) claims.get("email");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            UserDTO userDTO = new UserDTO(username, password, name, address, age, email, roleNames);

            log.info("멤버? {}", userDTO);
            log.info("멤버 권한? {}", userDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO, password, userDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //성공하면 다음목적지를 부른다.
            filterChain.doFilter(request,response); //통과
        } catch (Exception e) {
            log.info("에러 {}", e.getMessage());
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }

    }
}
