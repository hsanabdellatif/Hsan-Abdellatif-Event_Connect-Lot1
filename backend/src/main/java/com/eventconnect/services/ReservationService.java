package com.eventconnect.services;

import com.eventconnect.entities.Evenement;
import com.eventconnect.entities.Reservation;
import com.eventconnect.entities.StatutReservation;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.repositories.EvenementRepository;
import com.eventconnect.repositories.ReservationRepository;
import com.eventconnect.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des réservations
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;
    private final EvenementService evenementService;

    /**
     * Crée une nouvelle réservation
     * @param utilisateurId l'ID de l'utilisateur
     * @param evenementId l'ID de l'événement
     * @param nombrePlaces le nombre de places à réserver
     * @return la réservation créée
     * @throws RuntimeException si la réservation ne peut pas être créée
     */
    public Reservation creerReservation(Long utilisateurId, Long evenementId, Integer nombrePlaces) {
        log.info("Création d'une réservation - Utilisateur: {}, Événement: {}, Places: {}", 
                utilisateurId, evenementId, nombrePlaces);
        
        // Vérification de l'existence de l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        
        // Vérification de l'existence de l'événement
        Evenement evenement = evenementRepository.findById(evenementId)
            .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + evenementId));
        
        // Vérification que l'événement est dans le futur
        if (evenement.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de réserver un événement passé");
        }
        
        // Vérification qu'il n'existe pas déjà une réservation pour cet utilisateur et cet événement
        Optional<Reservation> reservationExistante = reservationRepository
            .findByUtilisateurAndEvenement(utilisateur, evenement);
        if (reservationExistante.isPresent()) {
            throw new RuntimeException("Une réservation existe déjà pour cet utilisateur et cet événement");
        }
        
        // Vérification de la disponibilité
        if (!evenementService.verifierDisponibilite(evenementId, nombrePlaces)) {
            throw new RuntimeException("Pas assez de places disponibles pour cet événement");
        }
        
        // Calcul du montant total
        BigDecimal montantTotal = evenement.getPrixPlace().multiply(new BigDecimal(nombrePlaces));
        
        // Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setEvenement(evenement);
        reservation.setNombrePlaces(nombrePlaces);
        reservation.setMontantTotal(montantTotal);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setDateReservation(LocalDateTime.now());
        
        Reservation nouvelleReservation = reservationRepository.save(reservation);
        log.info("Réservation créée avec succès, ID: {}", nouvelleReservation.getId());
        
        return nouvelleReservation;
    }

    /**
     * Confirme une réservation
     * @param reservationId l'ID de la réservation
     * @return la réservation confirmée
     * @throws RuntimeException si la réservation ne peut pas être confirmée
     */
    public Reservation confirmerReservation(Long reservationId) {
        log.info("Confirmation de la réservation ID: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + reservationId));
        
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new RuntimeException("Seules les réservations en attente peuvent être confirmées");
        }
        
        // Vérification que l'événement est toujours dans le futur
        if (reservation.getEvenement().getDateDebut().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de confirmer une réservation pour un événement passé");
        }
        
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setDateConfirmation(LocalDateTime.now());
        
        Reservation reservationConfirmee = reservationRepository.save(reservation);
        log.info("Réservation confirmée avec succès, ID: {}", reservationConfirmee.getId());
        
        return reservationConfirmee;
    }

    /**
     * Annule une réservation
     * @param reservationId l'ID de la réservation
     * @return la réservation annulée
     * @throws RuntimeException si la réservation ne peut pas être annulée
     */
    public Reservation annulerReservation(Long reservationId) {
        log.info("Annulation de la réservation ID: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + reservationId));
        
        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new RuntimeException("Cette réservation est déjà annulée");
        }
        
        reservation.setStatut(StatutReservation.ANNULEE);
        reservation.setDateAnnulation(LocalDateTime.now());
        
        Reservation reservationAnnulee = reservationRepository.save(reservation);
        log.info("Réservation annulée avec succès, ID: {}", reservationAnnulee.getId());
        
        return reservationAnnulee;
    }

    /**
     * Recherche une réservation par son ID
     * @param id l'ID de la réservation
     * @return la réservation si elle existe
     */
    @Transactional(readOnly = true)
    public Optional<Reservation> trouverParId(Long id) {
        log.info("Recherche de la réservation ID: {}", id);
        return reservationRepository.findById(id);
    }

    /**
     * Récupère toutes les réservations d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return liste des réservations de l'utilisateur
     */
    @Transactional(readOnly = true)
    public List<Reservation> obtenirReservationsUtilisateur(Long utilisateurId) {
        log.info("Récupération des réservations de l'utilisateur ID: {}", utilisateurId);
        
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));
        
        return reservationRepository.findByUtilisateur(utilisateur);
    }

    /**
     * Récupère toutes les réservations d'un événement
     * @param evenementId l'ID de l'événement
     * @return liste des réservations de l'événement
     */
    @Transactional(readOnly = true)
    public List<Reservation> obtenirReservationsEvenement(Long evenementId) {
        log.info("Récupération des réservations de l'événement ID: {}", evenementId);
        
        Evenement evenement = evenementRepository.findById(evenementId)
            .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + evenementId));
        
        return reservationRepository.findByEvenement(evenement);
    }

    /**
     * Récupère les réservations par statut
     * @param statut le statut recherché
     * @return liste des réservations avec ce statut
     */
    @Transactional(readOnly = true)
    public List<Reservation> obtenirReservationsParStatut(StatutReservation statut) {
        log.info("Récupération des réservations avec le statut: {}", statut);
        return reservationRepository.findByStatut(statut);
    }

    /**
     * Supprime une réservation
     * @param id l'ID de la réservation à supprimer
     * @throws RuntimeException si la réservation n'existe pas
     */
    public void supprimerReservation(Long id) {
        log.info("Suppression de la réservation ID: {}", id);
        
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Réservation non trouvée avec l'ID: " + id);
        }
        
        reservationRepository.deleteById(id);
        log.info("Réservation supprimée avec succès, ID: {}", id);
    }

    /**
     * Calcule le chiffre d'affaires total d'un événement
     * @param evenementId l'ID de l'événement
     * @return le chiffre d'affaires total
     */
    @Transactional(readOnly = true)
    public BigDecimal calculerChiffreAffairesEvenement(Long evenementId) {
        log.info("Calcul du chiffre d'affaires pour l'événement ID: {}", evenementId);
        
        BigDecimal chiffreAffaires = reservationRepository.calculateTotalRevenueForEvenement(evenementId);
        return chiffreAffaires != null ? chiffreAffaires : BigDecimal.ZERO;
    }

    /**
     * Compte le nombre total de réservations
     * @return le nombre de réservations
     */
    @Transactional(readOnly = true)
    public long compterReservations() {
        return reservationRepository.count();
    }

    /**
     * Compte le nombre de places réservées pour un événement
     * @param evenementId l'ID de l'événement
     * @return le nombre de places réservées
     */
    @Transactional(readOnly = true)
    public Integer compterPlacesReservees(Long evenementId) {
        log.info("Comptage des places réservées pour l'événement ID: {}", evenementId);
        
        Integer placesReservees = reservationRepository.countPlacesReserveesForEvenement(evenementId);
        return placesReservees != null ? placesReservees : 0;
    }
}
