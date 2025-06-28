package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import com.example.demo.entity.ViewingReservation;
import com.example.demo.entity.User;
import com.example.demo.entity.ViewingReservation.ReservationStatus;

public class ViewingReservationDto {
    @Schema(description = "Pageable", required = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageableDto {
        @Schema(description = "Page number", example = "0")
        private int page;

        @Schema(description = "Page size", example = "10")
        private int size;

        @Schema(description = "Sort", example = "id")
        private String sort;

        @Schema(description = "Direction", example = "asc")
        private String direction;
    }

    @Schema(description = "Viewing Reservation", required = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateViewingReservationDto {
        @Schema(description = "User ID", example = "1")
        private Long userId;

        @Schema(description = "Reservation Date", example = "2025-01-01 10:00:00")
        private LocalDateTime reservationDate;

        public ViewingReservation toEntity() {
            return ViewingReservation.builder()
                .user(User.builder().id(this.userId).build())
                .reservationDate(this.reservationDate)
                .build();
        }
    }

    @Schema(description = "Update Viewing Reservation", required = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateViewingReservationDto {
        @Schema(description = "Reservation Date", example = "2025-01-01 10:00:00")
        private LocalDateTime reservationDate;

        @Schema(description = "Status", example = "PENDING")
        private ReservationStatus status;

        public ViewingReservation toEntity(String id) {
            return ViewingReservation.builder()
                .id(id)
                .reservationDate(this.reservationDate)
                .status(this.status)
                .build();
        }
    }
}
