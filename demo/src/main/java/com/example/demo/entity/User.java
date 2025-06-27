package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 사용자 엔티티 클래스
 * NestJS의 @Entity() 데코레이터가 붙은 클래스와 동일한 역할
 * Spring Security의 UserDetails 인터페이스를 구현하여 인증 정보 제공
 * 
 * Lombok 어노테이션 설명:
 * @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
 * @NoArgsConstructor = 기본 생성자 자동 생성
 * @AllArgsConstructor = 모든 필드를 매개변수로 하는 생성자 생성
 * @Builder = 빌더 패턴 제공 (NestJS에는 없는 Java만의 강력한 기능)
 */
@Entity
@Table(name = "users") // NestJS TypeORM의 @Entity("users")와 같음
@Schema(description = "사용자 정보") // NestJS Swagger의 @ApiProperty()와 비슷
@Data // Lombok: 모든 Getter, Setter, toString, equals, hashCode 자동 생성!
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성 (JPA 필수)
@AllArgsConstructor // Lombok: 모든 필드 생성자 자동 생성
@Builder // Lombok: 빌더 패턴 제공
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // NestJS의 @PrimaryGeneratedColumn()과 같음
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Column(unique = true, nullable = false) // NestJS의 @Column({ unique: true })와 같음
    @NotBlank(message = "사용자명은 필수입니다") // NestJS의 class-validator와 비슷
    @Size(min = 3, max = 50, message = "사용자명은 3-50자 사이여야 합니다")
    @Schema(description = "사용자명", example = "john_doe")
    private String username;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Schema(description = "이메일", example = "john@example.com")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @Enumerated(EnumType.STRING) // NestJS의 enum 컬럼과 같음
    @Column(nullable = false)
    @Schema(description = "사용자 역할", example = "USER")
    @Builder.Default // Lombok 빌더에서 기본값 설정
    private Role role = Role.USER;

    @Column(name = "created_at")
    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    // NestJS TypeORM의 @BeforeInsert와 비슷
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // NestJS TypeORM의 @BeforeUpdate와 비슷
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Spring Security UserDetails 인터페이스 구현
    // NestJS의 Passport 전략에서 사용하는 validateUser와 비슷한 역할
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * 계정 만료 여부 (보통 premium 만료, 구독 만료 등에 사용)
     * true = 계정이 만료되지 않음 (정상)
     * false = 계정이 만료됨 (로그인 차단)
     * 
     * NestJS에서는 이런 기능을 직접 구현해야 하지만,
     * Spring Security는 기본 제공!
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 기능을 사용하지 않으므로 항상 true
    }

    /**
     * 계정 잠금 여부 (보통 로그인 실패 횟수 초과, 보안 정책 위반 등에 사용)
     * true = 계정이 잠기지 않음 (정상)
     * false = 계정이 잠김 (로그인 차단)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 기능을 사용하지 않으므로 항상 true
    }

    /**
     * 비밀번호 만료 여부 (보통 비밀번호 주기적 변경 정책에 사용)
     * true = 비밀번호가 만료되지 않음 (정상)
     * false = 비밀번호가 만료됨 (비밀번호 변경 필요)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 기능을 사용하지 않으므로 항상 true
    }

    /**
     * 계정 활성화 여부 (보통 이메일 인증, 관리자 승인 등에 사용)
     * true = 계정이 활성화됨 (로그인 가능)
     * false = 계정이 비활성화됨 (로그인 차단)
     * 
     * 실제 운영에서는 이메일 인증 여부나 관리자 승인 상태를 여기서 체크
     */
    @Override
    public boolean isEnabled() {
        return true; // 모든 계정을 활성화 상태로 처리
    }

    // 사용자 역할 열거형 (NestJS의 enum과 같음)
    public enum Role {
        USER, ADMIN
    }

    // ==================== Lombok 덕분에 아래 코드들이 모두 자동 생성됩니다! ====================
    // 
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // public String getUsername() { return username; }
    // public void setUsername(String username) { this.username = username; }
    // ... 기타 모든 getter/setter들
    // 
    // public String toString() { ... }
    // public boolean equals(Object o) { ... }
    // public int hashCode() { ... }
    //
    // User.builder()
    //     .username("john")
    //     .email("john@example.com")
    //     .password("password")
    //     .role(Role.USER)
    //     .build();
    // 
    // ========================================================================================
} 