package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger API 문서화 설정
 * NestJS의 SwaggerModule.setup()과 동일한 역할
 * API 문서를 자동으로 생성하고 UI를 제공
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 3.0 설정
     * NestJS의 DocumentBuilder와 비슷한 역할
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Live Auction Market API") // NestJS: setTitle()
                        .description("Spring Boot + JPA + Security + WebSocket + MySQL 예제 API") // NestJS: setDescription()
                        .version("1.0.0") // NestJS: setVersion()
                        .contact(new Contact()
                                .name("개발팀")
                                .email("dev@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.example.com")
                                .description("운영 서버")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic Authentication")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}