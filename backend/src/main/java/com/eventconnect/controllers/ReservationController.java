package com.eventconnect.controllers;

import com.eventconnect.dto.ReservationDTO;
import com.eventconnect.dto.ReservationStats;
import com.eventconnect.entities.Reservation;
import com.eventconnect.services.ReservationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * @version 2.0.1
 */
@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = {"http://localhost:3000", "https://mon-app.com"})
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Crée une nouvelle réservation
     * @param dto les données de la réservation
     * @return la réservation créée
     */
    @PostMapping
    public ResponseEntity<Reservation> creerReservation(@Valid @RequestBody ReservationDTO dto) {
        log.info("POST /reservations - Création d'une réservation (utilisateur: {}, événement: {}, places: {})",
                dto.getUtilisateurId(), dto.getEvenementId(), dto.getNombrePlaces());

        try {
            Reservation nouvelleReservation = reservationService.creerReservation(dto.getUtilisateurId(), dto.getEvenementId(), dto.getNombrePlaces());
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleReservation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère une réservation par son ID
     * @param id l'ID de la réservation
     * @return la réservation trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> obtenirReservation(@PathVariable Long id) {
        log.info("GET /reservations/{} - Récupération de la réservation", id);

        Optional<Reservation> reservation = reservationService.trouverParId(id);
        return reservation
                .map(r -> ResponseEntity.ok().body(r))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Confirme une réservation
     * @param id l'ID de la réservation à confirmer
     * @return la réservation confirmée
     */
    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<Reservation> confirmerReservation(@PathVariable Long id) {
        log.info("PATCH /reservations/{}/confirmer - Confirmation de la réservation", id);

        try {
            Reservation reservationConfirmee = reservationService.confirmerReservation(id);
            return ResponseEntity.ok(reservationConfirmee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la confirmation de la réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Annule une réservation
     * @param id l'ID de la réservation à annuler
     * @return la réservation annulée
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Reservation> annulerReservation(@PathVariable Long id) {
        log.info("PATCH /reservations/{}/annuler - Annulation de la réservation", id);

        try {
            Reservation reservationAnnulee = reservationService.annulerReservation(id);
            return ResponseEntity.ok(reservationAnnulee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'annulation de la réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Rembourse une réservation
     * @param id l'ID de la réservation à rembourser
     * @return la réservation remboursée
     */
    @PatchMapping("/{id}/rembourser")
    public ResponseEntity<Reservation> rembourserReservation(@PathVariable Long id) {
        log.info("PATCH /reservations/{}/rembourser - Remboursement de la réservation", id);

        try {
            Reservation reservationRemboursee = reservationService.rembourserReservation(id);
            return ResponseEntity.ok(reservationRemboursee);
        } catch (RuntimeException e) {
            log.error("Erreur lors du remboursement: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère toutes les réservations d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return liste des réservations de l'utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Reservation>> obtenirReservationsUtilisateur(@PathVariable Long utilisateurId) {
        log.info("GET /reservations/utilisateur/{} - Récupération des réservations de l'utilisateur", utilisateurId);

        try {
            List<Reservation> reservations = reservationService.obtenirReservationsUtilisateur(utilisateurId);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des réservations utilisateur: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupère toutes les réservations d'un événement
     * @param evenementId l'ID de l'événement
     * @return liste des réservations de l'événement
     */
    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<Reservation>> obtenirReservationsEvenement(@PathVariable Long evenementId) {
        log.info("GET /reservations/evenement/{} - Récupération des réservations de l'événement", evenementId);

        try {
            List<Reservation> reservations = reservationService.obtenirReservationsEvenement(evenementId);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des réservations événement: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupère les réservations par statut
     * @param statut le statut recherché
     * @return liste des réservations avec ce statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Reservation>> obtenirReservationsParStatut(@PathVariable Reservation.StatutReservation statut) {
        log.info("GET /reservations/statut/{} - Récupération des réservations par statut", statut);

        try {
            List<Reservation> reservations = reservationService.obtenirReservationsParStatut(statut);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des réservations par statut: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprime une réservation
     * @param id l'ID de la réservation à supprimer
     * @return réponse de suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerReservation(@PathVariable Long id) {
        log.info("DELETE /reservations/{} - Suppression de la réservation", id);

        try {
            reservationService.supprimerReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de la réservation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Calcule le chiffre d'affaires total d'un événement
     * @param evenementId l'ID de l'événement
     * @return le chiffre d'affaires total
     */
    @GetMapping("/evenement/{evenementId}/chiffre-affaires")
    public ResponseEntity<BigDecimal> calculerChiffreAffairesEvenement(@PathVariable Long evenementId) {
        log.info("GET /reservations/evenement/{}/chiffre-affaires - Calcul du chiffre d'affaires", evenementId);

        try {
            BigDecimal chiffreAffaires = reservationService.calculerChiffreAffairesEvenement(evenementId);
            return ResponseEntity.ok(chiffreAffaires);
        } catch (RuntimeException e) {
            log.error("Erreur lors du calcul du chiffre d'affaires: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Compte le nombre total de réservations
     * @return le nombre de réservations
     */
    @GetMapping("/count")
    public ResponseEntity<Long> compterReservations() {
        log.info("GET /reservations/count - Comptage des réservations");

        try {
            long nombreReservations = reservationService.compterReservations();
            return ResponseEntity.ok(nombreReservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors du comptage des réservations: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère les statistiques des réservations
     * @return les statistiques des réservations
     */
    @GetMapping("/stats")
    public ResponseEntity<ReservationStats> obtenirStatistiques() {
        log.info("GET /reservations/stats - Récupération des statistiques des réservations");

        try {
            ReservationStats stats = reservationService.calculerStatistiques();
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Compte le nombre de places réservées pour un événement
     * @param evenementId l'ID de l'événement
     * @return le nombre de places réservées
     */
    @GetMapping("/evenement/{evenementId}/places-reservees")
    public ResponseEntity<Integer> compterPlacesReservees(@PathVariable Long evenementId) {
        log.info("GET /reservations/evenement/{}/places-reservees - Comptage des places réservées", evenementId);

        try {
            Integer placesReservees = reservationService.compterPlacesReservees(evenementId);
            return ResponseEntity.ok(placesReservees);
        } catch (RuntimeException e) {
            log.error("Erreur lors du comptage des places réservées: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère toutes les réservations avec pagination
     * @param page numéro de la page
     * @param size taille de la page
     * @return liste des réservations
     */
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        log.info("GET /reservations - Récupération de toutes les réservations");

        try {
            Pageable pageable = PageRequest.of(page, size);
            List<Reservation> reservations = reservationService.obtenirToutesReservations(pageable);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des réservations: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Met à jour une réservation
     * @param id l'ID de la réservation
     * @param dto les données mises à jour
     * @return la réservation mise à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationDTO dto) {
        log.info("PUT /reservations/{} - Mise à jour de la réservation", id);

        try {
            Reservation updated = reservationService.mettreAJourReservation(id, dto.getNombrePlaces(), dto.getCommentaires());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}