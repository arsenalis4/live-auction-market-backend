package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 설정 클래스
 * SecurityConfig와 UserService 간의 순환 참조를 방지하기 위해 분리
 * NestJS에서 별도 Provider로 bcrypt를 설정하는 것과 비슷
 */
@Configuration
public class PasswordConfig {

    /**
     * 비밀번호 암호화 Bean
     * NestJS에서 bcrypt 사용과 같음
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 