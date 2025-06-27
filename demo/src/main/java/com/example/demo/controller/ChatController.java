package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket 채팅 컨트롤러
 * NestJS의 @WebSocketGateway() 클래스와 동일한 역할
 * 실시간 메시징을 처리하는 WebSocket 엔드포인트들을 제공
 */
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 공개 채팅 메시지 처리
     * NestJS: @SubscribeMessage('message') 데코레이터와 비슷
     * 
     * 사용법: 클라이언트에서 /app/chat.sendMessage 로 메시지 전송
     * 결과: /topic/public 구독자들에게 브로드캐스트
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // NestJS에서 @MessageBody() body와 비슷한 역할
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }

    /**
     * 사용자 입장 처리
     * NestJS: @SubscribeMessage('join') 데코레이터와 비슷
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, Principal principal) {
        // Principal은 NestJS의 @ConnectedSocket() socket.user와 비슷
        chatMessage.setType(MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setContent(chatMessage.getSender() + "님이 입장하셨습니다!");
        return chatMessage;
    }

    /**
     * 개인 메시지 전송
     * NestJS에서 socket.to(userId).emit()과 비슷한 기능
     */
    @MessageMapping("/chat.sendPrivate")
    public void sendPrivateMessage(@Payload Map<String, Object> message, Principal principal) {
        String recipient = (String) message.get("recipient");
        String content = (String) message.get("content");
        
        ChatMessage privateMessage = new ChatMessage();
        privateMessage.setSender(principal.getName());
        privateMessage.setContent(content);
        privateMessage.setType(MessageType.PRIVATE);
        privateMessage.setTimestamp(LocalDateTime.now());
        
        // 특정 사용자에게만 메시지 전송
        // NestJS: server.to(socketId).emit('privateMessage', data)와 비슷
        messagingTemplate.convertAndSendToUser(recipient, "/queue/private", privateMessage);
    }

    /**
     * WebSocket 연결 테스트용 REST 엔드포인트
     * 실제 운영에서는 제거해도 됨
     */
    @GetMapping("/api/chat/test")
    @ResponseBody
    public String testChat() {
        return "WebSocket Chat is running! Connect to /ws endpoint.";
    }

    /**
     * 서버에서 직접 메시지 브로드캐스트 (예: 시스템 공지)
     * NestJS에서 server.emit()과 비슷한 기능
     */
    public void broadcastSystemMessage(String message) {
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setSender("System");
        systemMessage.setContent(message);
        systemMessage.setType(MessageType.SYSTEM);
        systemMessage.setTimestamp(LocalDateTime.now());
        
        messagingTemplate.convertAndSend("/topic/public", systemMessage);
    }

    /**
     * 채팅 메시지 DTO
     * NestJS의 interface ChatMessage와 동일한 역할
     */
    public static class ChatMessage {
        private MessageType type;
        private String content;
        private String sender;
        private LocalDateTime timestamp;

        // 기본 생성자
        public ChatMessage() {}

        // 생성자
        public ChatMessage(MessageType type, String content, String sender) {
            this.type = type;
            this.content = content;
            this.sender = sender;
            this.timestamp = LocalDateTime.now();
        }

        // Getters and Setters
        public MessageType getType() {
            return type;
        }

        public void setType(MessageType type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * 메시지 타입 열거형
     * NestJS의 enum MessageType과 동일
     */
    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 사용자 입장
        LEAVE,   // 사용자 퇴장
        PRIVATE, // 개인 메시지
        SYSTEM   // 시스템 메시지
    }
} 