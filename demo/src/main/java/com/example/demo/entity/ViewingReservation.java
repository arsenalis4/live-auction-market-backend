package com.example.demo.entity;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "viewing_reservations")
@Schema(description = "영화 예매 정보")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewingReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "영화 예매 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "사용자")
    private User user;
    
    @Column(name = "status")
    @Schema(description = "영화 예매 상태", example = "예매완료")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(name = "reservation_date")
    @Schema(description = "영화 예매 일시", example = "2025-01-01 10:00:00")
    private LocalDateTime reservationDate;

    @Column(name = "created_at")
    @Schema(description = "생성일시", example = "2025-01-01 10:00:00")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }
}