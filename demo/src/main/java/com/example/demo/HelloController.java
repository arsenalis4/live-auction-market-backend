package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "안녕하세요!";
    }

    @GetMapping("/health")
    public String health() {
        return "서버가 정상적으로 동작중입니다!";
    }
} 