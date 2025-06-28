package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ViewingReservationService;
import com.example.demo.entity.ViewingReservation;
import com.example.demo.entity.ViewingReservation.ReservationStatus;
import com.example.demo.dto.ViewingReservationDto.*;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;



@RestController
@RequestMapping("/api/viewing-reservations")
@Tag(name = "Viewing Reservation", description = "Viewing Reservation API")
public class ViewingReservationController {
    private final ViewingReservationService viewingReservationService;
    private final UserService userService;

    public ViewingReservationController(ViewingReservationService viewingReservationService, UserService userService) {
        this.viewingReservationService = viewingReservationService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all viewing reservations", description = "Get all viewing reservations")
    public ResponseEntity<Page<ViewingReservation>> findAll(PageableDto pageableDto) {
        Pageable pageable = PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), Sort.by(pageableDto.getSort()).descending());
        return ResponseEntity.ok(viewingReservationService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get viewing reservation by id", description = "Get viewing reservation by id")
    public ResponseEntity<ViewingReservation> findById(@PathVariable String id) {
        return ResponseEntity.ok(viewingReservationService.getViewingReservationById(id));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get viewing reservations by user id", description = "Get viewing reservations by user id")
    public ResponseEntity<List<ViewingReservation>> findByUserId(@PathVariable Long userId, @RequestParam(required = false) ReservationStatus reservationStatus) {
        return ResponseEntity.ok(viewingReservationService.getViewingReservationsByUserId(userId, reservationStatus));
    }

    @PostMapping
    @Operation(summary = "Create viewing reservation", description = "Create viewing reservation")
    public ResponseEntity<ViewingReservation> create(@RequestBody CreateViewingReservationDto createViewingReservationDto) {
        ViewingReservation viewingReservation = createViewingReservationDto.toEntity();
        return ResponseEntity.ok(viewingReservationService.createViewingReservation(viewingReservation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update viewing reservation", description = "Update viewing reservation")
    public ResponseEntity<ViewingReservation> update(@PathVariable String id, @RequestBody ViewingReservation viewingReservation) {
        // ViewingReservation viewingReservation = updateViewingReservationDto.toEntity(id);
        return ResponseEntity.ok(viewingReservationService.updateViewingReservation(id, viewingReservation));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete viewing reservation", description = "Delete viewing reservation")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        viewingReservationService.deleteViewingReservation(id);
        return ResponseEntity.noContent().build();
    }
}
