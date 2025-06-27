package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정 클래스
 * NestJS의 AuthModule, Guards, Passport strategies 등의 기능을 모두 포함
 * 인증, 인가, CORS 등의 보안 설정을 담당
 */
@Configuration
@EnableWebSecurity // NestJS의 AuthModule과 비슷한 역할
@EnableMethodSecurity(prePostEnabled = true) // NestJS의 @UseGuards() 데코레이터와 비슷
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 인증 제공자 설정
     * NestJS의 LocalStrategy와 비슷한 역할
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * 인증 매니저 Bean
     * NestJS의 AuthService와 비슷한 역할
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS 설정
     * NestJS의 app.enableCors()와 같음
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * HTTP 보안 설정
     * NestJS의 Guards와 전체적인 보안 파이프라인 설정과 비슷
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // NestJS에서 CSRF 비활성화와 같음
            .authorizeHttpRequests(authz -> authz
                // 공개 엔드포인트 (NestJS의 @Public() 데코레이터와 비슷)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users").permitAll() // 사용자 등록
                .requestMatchers("/ws/**").permitAll() // WebSocket
                
                // Swagger UI 접근 허용
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // 정적 리소스 접근 허용
                .requestMatchers("/css/**", "/js/**", "/images/**", "/*.html").permitAll()
                
                // 관리자 전용 엔드포인트 (NestJS의 @Roles('admin') 데코레이터와 비슷)
                .requestMatchers("/api/users/paginated").hasRole("ADMIN")
                .requestMatchers("/api/users/role/**").hasRole("ADMIN")
                
                // 나머지 모든 요청은 인증 필요 (NestJS의 기본 AuthGuard와 비슷)
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.realmName("Demo API")) // 기본 HTTP 인증 사용
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
} 