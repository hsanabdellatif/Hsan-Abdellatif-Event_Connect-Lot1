package com.eventconnect.controllers;

import com.eventconnect.dto.ReservationDTO;
import com.eventconnect.entities.Reservation;
import com.eventconnect.mappers.ReservationMapper;
import com.eventconnect.repositories.ReservationRepository;
import com.eventconnect.services.ReservationService;
import com.eventconnect.dto.ReservationStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des réservations
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository; // Added
    private final ReservationMapper reservationMapper; // Added

    public ReservationController(ReservationService reservationService,
                                 ReservationRepository reservationRepository,
                                 ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository; // Initialize
        this.reservationMapper = reservationMapper; // Initialize
    }

    // ... (other methods remain unchanged until getAllReservations and updateReservation)

    /**
     * Récupère toutes les réservations
     * @return liste de toutes les réservations actives
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("GET /reservations - Récupération de toutes les réservations");
        try {
            List<Reservation> reservations = reservationRepository.findByActifTrue();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Met à jour une réservation
     * @param id l'ID de la réservation
     * @param dto les données mises à jour
     * @return la réservation mise à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody ReservationDTO dto) {
        log.info("PUT /reservations/{} - Mise à jour de la réservation", id);
        try {
            Reservation reservation = reservationService.trouverParId(id)
                    .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
            // Update fields from DTO
            reservationMapper.updateEntityFromDTO(reservation, dto);
            Reservation updated = reservationRepository.save(reservation);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ... (other methods remain unchanged)
}