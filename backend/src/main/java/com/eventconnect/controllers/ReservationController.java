package com.eventconnect.controllers;

import com.eventconnect.entities.Reservation;
import com.eventconnect.entities.StatutReservation;
import com.eventconnect.services.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Crée une nouvelle réservation
     * @param utilisateurId l'ID de l'utilisateur
     * @param evenementId l'ID de l'événement
     * @param nombrePlaces le nombre de places à réserver
     * @return la réservation créée
     */
    @PostMapping
    public ResponseEntity<Reservation> creerReservation(
            @RequestParam Long utilisateurId,
            @RequestParam Long evenementId,
            @RequestParam Integer nombrePlaces) {
        
        log.info("POST /api/reservations - Création d'une réservation (utilisateur: {}, événement: {}, places: {})", 
                utilisateurId, evenementId, nombrePlaces);
        
        try {
            Reservation nouvelleReservation = reservationService.creerReservation(utilisateurId, evenementId, nombrePlaces);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleReservation);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la création de la réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de la réservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère une réservation par son ID
     * @param id l'ID de la réservation
     * @return la réservation trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> obtenirReservation(@PathVariable Long id) {
        log.info("GET /api/reservations/{} - Récupération de la réservation", id);
        
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
    @PutMapping("/{id}/confirmer")
    public ResponseEntity<Reservation> confirmerReservation(@PathVariable Long id) {
        log.info("PUT /api/reservations/{}/confirmer - Confirmation de la réservation", id);
        
        try {
            Reservation reservationConfirmee = reservationService.confirmerReservation(id);
            return ResponseEntity.ok(reservationConfirmee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la confirmation de la réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la confirmation de la réservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Annule une réservation
     * @param id l'ID de la réservation à annuler
     * @return la réservation annulée
     */
    @PutMapping("/{id}/annuler")
    public ResponseEntity<Reservation> annulerReservation(@PathVariable Long id) {
        log.info("PUT /api/reservations/{}/annuler - Annulation de la réservation", id);
        
        try {
            Reservation reservationAnnulee = reservationService.annulerReservation(id);
            return ResponseEntity.ok(reservationAnnulee);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'annulation de la réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'annulation de la réservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère toutes les réservations d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return liste des réservations de l'utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Reservation>> obtenirReservationsUtilisateur(@PathVariable Long utilisateurId) {
        log.info("GET /api/reservations/utilisateur/{} - Récupération des réservations de l'utilisateur", utilisateurId);
        
        try {
            List<Reservation> reservations = reservationService.obtenirReservationsUtilisateur(utilisateurId);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des réservations utilisateur: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des réservations utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère toutes les réservations d'un événement
     * @param evenementId l'ID de l'événement
     * @return liste des réservations de l'événement
     */
    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<Reservation>> obtenirReservationsEvenement(@PathVariable Long evenementId) {
        log.info("GET /api/reservations/evenement/{} - Récupération des réservations de l'événement", evenementId);
        
        try {
            List<Reservation> reservations = reservationService.obtenirReservationsEvenement(evenementId);
            return ResponseEntity.ok(reservations);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des réservations événement: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des réservations événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les réservations par statut
     * @param statut le statut recherché
     * @return liste des réservations avec ce statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Reservation>> obtenirReservationsParStatut(@PathVariable StatutReservation statut) {
        log.info("GET /api/reservations/statut/{} - Récupération des réservations par statut", statut);
        
        try {
            List<Reservation> reservations = reservationService.obtenirReservationsParStatut(statut);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations par statut", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime une réservation
     * @param id l'ID de la réservation à supprimer
     * @return réponse de suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerReservation(@PathVariable Long id) {
        log.info("DELETE /api/reservations/{} - Suppression de la réservation", id);
        
        try {
            reservationService.supprimerReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de la réservation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de la réservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Calcule le chiffre d'affaires total d'un événement
     * @param evenementId l'ID de l'événement
     * @return le chiffre d'affaires total
     */
    @GetMapping("/evenement/{evenementId}/chiffre-affaires")
    public ResponseEntity<BigDecimal> calculerChiffreAffairesEvenement(@PathVariable Long evenementId) {
        log.info("GET /api/reservations/evenement/{}/chiffre-affaires - Calcul du chiffre d'affaires", evenementId);
        
        try {
            BigDecimal chiffreAffaires = reservationService.calculerChiffreAffairesEvenement(evenementId);
            return ResponseEntity.ok(chiffreAffaires);
        } catch (Exception e) {
            log.error("Erreur lors du calcul du chiffre d'affaires", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Compte le nombre total de réservations
     * @return le nombre de réservations
     */
    @GetMapping("/count")
    public ResponseEntity<Long> compterReservations() {
        log.info("GET /api/reservations/count - Comptage des réservations");
        
        try {
            long nombreReservations = reservationService.compterReservations();
            return ResponseEntity.ok(nombreReservations);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des réservations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Compte le nombre de places réservées pour un événement
     * @param evenementId l'ID de l'événement
     * @return le nombre de places réservées
     */
    @GetMapping("/evenement/{evenementId}/places-reservees")
    public ResponseEntity<Integer> compterPlacesReservees(@PathVariable Long evenementId) {
        log.info("GET /api/reservations/evenement/{}/places-reservees - Comptage des places réservées", evenementId);
        
        try {
            Integer placesReservees = reservationService.compterPlacesReservees(evenementId);
            return ResponseEntity.ok(placesReservees);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des places réservées", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
