package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.entity.ViewingReservation;
import com.example.demo.entity.ViewingReservation.ReservationStatus;
import com.example.demo.repository.ViewingReservationRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ViewingReservationService {
    private final ViewingReservationRepository viewingReservationRepository;

    @Autowired
    public ViewingReservationService(ViewingReservationRepository viewingReservationRepository) {
        this.viewingReservationRepository = viewingReservationRepository;
    }

    public Page<ViewingReservation> findAll(Pageable pageable) {
        return viewingReservationRepository.findAll(pageable);
    }

    public ViewingReservation getViewingReservationById(String id) {
        return viewingReservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Viewing reservation not found"));
    }

    public List<ViewingReservation> getViewingReservationsByUserId(Long userId, ReservationStatus reservationStatus) {
        return viewingReservationRepository.findByUserIdAndStatus(userId, reservationStatus);
    }

    @Transactional
    public ViewingReservation createViewingReservation(ViewingReservation viewingReservation) {
        return viewingReservationRepository.save(viewingReservation);
    }

    @Transactional
    public ViewingReservation updateViewingReservation(String id, ViewingReservation viewingReservation) {
        return viewingReservationRepository.save(viewingReservation);
    }

    @Transactional
    public ViewingReservation updateViewingReservationStatus(String id, ViewingReservation.ReservationStatus status) {
        ViewingReservation viewingReservation = getViewingReservationById(id);
        viewingReservation.setStatus(status);
        return viewingReservationRepository.save(viewingReservation);
    }

    @Transactional
    public void deleteViewingReservation(String id) {
        ViewingReservation viewingReservation = getViewingReservationById(id);

        if (viewingReservation == null) {
            throw new RuntimeException("Viewing reservation not found");
        }

        viewingReservationRepository.delete(viewingReservation);
    }
}
