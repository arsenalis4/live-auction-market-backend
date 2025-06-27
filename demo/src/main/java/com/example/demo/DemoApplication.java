package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * 메인 Spring Boot 애플리케이션 클래스
 * NestJS의 main.ts와 app.module.ts의 역할을 함께 합니다
 */
@SpringBootApplication // NestJS의 @Module() 데코레이터와 비슷하지만 더 포괄적
@EnableJpaRepositories // NestJS의 TypeOrmModule.forRoot()와 비슷한 JPA 활성화
@EnableWebSocketMessageBroker // NestJS의 WebSocket Gateway 활성화와 비슷
public class DemoApplication {

	public static void main(String[] args) {
		// NestJS의 NestFactory.create()와 app.listen()을 합친 것과 비슷
		SpringApplication.run(DemoApplication.class, args);
	}

}
