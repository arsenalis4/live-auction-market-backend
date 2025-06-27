package com.example.demo.dto;

import com.example.demo.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO 클래스
 * NestJS에서 클라이언트에게 반환할 때 사용하는 Response DTO와 동일한 역할
 * 비밀번호 같은 민감한 정보는 제외하고 반환
 */
@Schema(description = "사용자 정보 응답")
public class UserResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자명", example = "john_doe")
    private String username;

    @Schema(description = "이메일", example = "john@example.com")
    private String email;

    @Schema(description = "사용자 역할", example = "USER")
    private User.Role role;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    // 기본 생성자
    public UserResponseDto() {}

    // 생성자
    public UserResponseDto(Long id, String username, String email, User.Role role, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Entity에서 DTO로 변환하는 정적 메서드
    // NestJS에서 Entity를 Response DTO로 변환할 때와 비슷
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 