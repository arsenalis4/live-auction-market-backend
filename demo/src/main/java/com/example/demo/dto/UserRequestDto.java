package com.example.demo.dto;

import com.example.demo.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 사용자 요청 DTO 클래스
 * NestJS의 CreateUserDto, UpdateUserDto와 동일한 역할
 * 클라이언트로부터 받는 데이터를 검증하고 전달하는 역할
 */
@Schema(description = "사용자 등록/수정 요청")
public class UserRequestDto {

    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3-50자 사이여야 합니다")
    @Schema(description = "사용자명", example = "john_doe", required = true)
    private String username;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Schema(description = "이메일", example = "john@example.com", required = true)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    @Schema(description = "비밀번호", example = "password123", required = true)
    private String password;

    @Schema(description = "사용자 역할", example = "USER", allowableValues = {"USER", "ADMIN"})
    private User.Role role = User.Role.USER;

    // 기본 생성자
    public UserRequestDto() {}

    // 생성자
    public UserRequestDto(String username, String email, String password, User.Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Entity로 변환하는 메서드 (NestJS에서 직접 Entity 생성할 때와 비슷)
    public User toEntity() {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
} 