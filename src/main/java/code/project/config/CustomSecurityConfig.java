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
import org.springframework.security.config.http.SessionCreationPolicy;
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

        http.cors(c -> c.configurationSource(corsConfigurationSource()));
        http.csrf(csrf -> csrf.disable());

        // ★ Stateless
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendError(401))
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );

        http.authorizeHttpRequests(auth -> auth
                // 정적 리소스
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // 기본/헬스체크
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers("/__health", "/project/health", "/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // 회원가입/계정 관련
                .requestMatchers(HttpMethod.POST, "/project/register").permitAll()
                .requestMatchers("/api/account/**", "/api/password/**").permitAll()

                // 영업시간/시설
                .requestMatchers("/project/open-hours/**", "/project/facility/*/business-hours").permitAll()
                .requestMatchers("/project/nmc/**").permitAll()
                .requestMatchers("/", "/error", "/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()

                // 카카오 로그인
                .requestMatchers("/project/user/kakao").permitAll()

                // JWT 로그인/리프레시
                .requestMatchers("/project/user/login", "/project/user/logout", "/project/user/refresh").permitAll()
                .requestMatchers("/project/user/me").authenticated()

                // 병원/약국/시설 검색
                .requestMatchers("/project/hospital/**", "/project/pharmacy/**", "/project/facility/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/project/facility/*/open").permitAll()
                .requestMatchers(HttpMethod.POST, "/project/facility/open-batch").permitAll()
                .requestMatchers("/project/realtime/**").permitAll()
                .requestMatchers("/files/**", "/uploads/**", "/default-profile.png").permitAll()
                .requestMatchers("/project/map/**").permitAll()

                // ====== 여기 수정됨 (support 좋아요 경로) ======
                // 기존: "/project/support/**/likes/status", "/project/support/**/likes"
                // → Spring 6 PathPattern에서 ** 뒤에 추가 segment가 오면 예외 발생
                .requestMatchers(HttpMethod.GET,   "/project/support/*/likes/status").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/project/support/*/likes").authenticated()
                // 나머지 support 경로
                .requestMatchers(HttpMethod.GET,    "/project/support/**").permitAll()
                .requestMatchers(HttpMethod.POST,   "/project/support/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/project/support/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/project/support/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/project/support/**").hasRole("ADMIN")
                // ==============================

                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ★ 게시판 공개/보호 명시
                .requestMatchers("/api/posts/**").authenticated()

                // ★ 즐겨찾기 보호 명시
                .requestMatchers(HttpMethod.GET, "/project/favorite/my").authenticated()

                // 나머지는 전부 인증 필요
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

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
