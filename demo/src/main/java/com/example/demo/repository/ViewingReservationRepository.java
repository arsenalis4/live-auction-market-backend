package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.ViewingReservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewingReservationRepository extends JpaRepository<ViewingReservation, String> {
    Optional<ViewingReservation> findById(String id);

    @Query(value = "SELECT v FROM ViewingReservation v WHERE v.user.id = :userId AND (:status IS NULL OR v.status = :status)")
    List<ViewingReservation> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ViewingReservation.ReservationStatus status);
}
