package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 * NestJS의 @WebSocketGateway() 데코레이터와 전체 WebSocket 모듈 설정과 비슷한 역할
 * STOMP 프로토콜을 사용한 WebSocket 통신을 설정
 */
@Configuration
@EnableWebSocketMessageBroker // NestJS의 WebSocket 모듈 활성화와 비슷
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Message Broker 설정
     * NestJS에서 Socket.IO의 namespace/room 개념과 비슷
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 때 사용할 prefix
        // NestJS: @SubscribeMessage('topic/auction') 와 비슷
        config.enableSimpleBroker("/topic", "/queue");
        
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 prefix
        // NestJS: @MessageBody() 로 받는 메시지들의 경로 설정과 비슷
        config.setApplicationDestinationPrefixes("/app");
        
        // 특정 사용자에게 개인 메시지를 보낼 때 사용할 prefix
        // NestJS에서 socket.to(userId).emit() 과 비슷한 개념
        config.setUserDestinationPrefix("/user");
    }

    /**
     * STOMP 엔드포인트 설정
     * NestJS에서 WebSocket 연결 엔드포인트 설정과 비슷
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트 등록
        // NestJS: @WebSocketGateway(port, { path: '/ws' }) 와 비슷
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS fallback 지원 (NestJS의 Socket.IO와 비슷한 역할)
    }
} 