package code.project.config;

import code.project.security.handler.APILoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("..........security config");

        //CORS 정책사용
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        //CSRF 사용하지 않음
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        http.formLogin(config -> {
            config.loginPage("/api/user/login");
            config.successHandler(new APILoginSuccessHandler());
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        //CorsConfiguration 객체를 생성합니다. 이 객체는 CORS 정책을 정의
        CorsConfiguration configuration = new CorsConfiguration();
        //모든 Origin을 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        //허용할 HTTP 메서드를 설정
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        //허용할 HTTP 요청 헤더를 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        //클라이언트가 **자격 증명(쿠키, 인증 정보 등)**을 포함한 요청을 보낼 수 있도록 허용
        //true로 설정되면, 서버는 자격 증명 있는 요청을 신뢰
        configuration.setAllowCredentials(true);

        //URL 패턴에 따라 CorsConfiguration을 매핑할 수 있는 객체를 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        //"/**"로 지정하여 모든 URL 경로에 대해 이 CORS 정책을 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
