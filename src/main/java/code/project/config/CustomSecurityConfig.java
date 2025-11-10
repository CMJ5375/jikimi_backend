package code.project.config;

import code.project.security.filter.JWTCheckFilter;
import code.project.security.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("..........security config (no formLogin / no httpBasic)");

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf.disable());

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendError(401))
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        http.authorizeHttpRequests(auth -> auth
                // 정적 리소스
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // 헬스/루트
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers("/__health", "/project/health", "/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // 공개 엔드포인트
                .requestMatchers(HttpMethod.POST, "/project/register").permitAll()
                .requestMatchers("/api/account/**", "/api/password/**").permitAll()
                .requestMatchers("/project/open-hours/**", "/project/facility/*/business-hours").permitAll()
                .requestMatchers("/project/nmc/**").permitAll()
                .requestMatchers("/", "/error", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()

                // ===== Kakao OAuth 공개 =====
                .requestMatchers(HttpMethod.GET,  "/project/user/kakao").permitAll()
                .requestMatchers(HttpMethod.POST, "/project/user/kakao/token").permitAll()

                // 로그인 관련만 공개 (전체 /project/user/** permitAll 제거 상태 유지)
                .requestMatchers("/project/user/login", "/project/user/logout", "/project/user/refresh").permitAll()
                // me는 인증 필요
                .requestMatchers("/project/user/me").authenticated()

                // 공개 조회 성격
                .requestMatchers("/project/hospital/**", "/project/pharmacy/**", "/project/facility/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/project/facility/*/open").permitAll()
                .requestMatchers(HttpMethod.POST, "/project/facility/open-batch").permitAll()
                .requestMatchers("/project/realtime/**").permitAll()
                .requestMatchers("/files/**", "/uploads/**", "/default-profile.png").permitAll()
                .requestMatchers("/project/map/**").permitAll()

                // 지원/FAQ/자료실
                .requestMatchers(HttpMethod.GET, "/project/support/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/project/support/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/project/support/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/project/support/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/project/support/**").hasRole("ADMIN")

                // Preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 게시판
                .requestMatchers(HttpMethod.GET,
                        "/api/posts/list",
                        "/api/posts/hot/pins",
                        "/api/posts/*/likes/status",
                        "/api/posts/**"
                ).permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/posts/*/views").permitAll()
                .requestMatchers(HttpMethod.POST,   "/api/posts/add").authenticated()
                .requestMatchers(HttpMethod.PUT,    "/api/posts/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/*").authenticated()
                .requestMatchers(HttpMethod.PATCH,  "/api/posts/*/likes").authenticated()

                // 기타
                .anyRequest().authenticated()
        );

        // formLogin/httpBasic 비활성 (네 설정 유지)
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // JWT 필터
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 기본 strength=10
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(java.util.List.of(
                "http://localhost:3000",
                "https://localhost:3000",
                "https://jikimi.duckdns.org",
                "http://apiserver-env.eba-wqmpyrjp.ap-northeast-2.elasticbeanstalk.com",
                "https://apiserver-env.eba-wqmpyrjp.ap-northeast-2.elasticbeanstalk.com",
                "https://d3s30j0qk5vpe1.cloudfront.net"
        ));
        cfg.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "Cache-Control", "X-Requested-With"));
        cfg.setAllowCredentials(true);
        cfg.setExposedHeaders(java.util.List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
