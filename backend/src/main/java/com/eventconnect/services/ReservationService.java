package com.eventconnect.services;

import com.eventconnect.entities.Evenement;
import com.eventconnect.entities.Reservation;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.repositories.EvenementRepository;
import com.eventconnect.repositories.ReservationRepository;
import com.eventconnect.repositories.UtilisateurRepository;
import com.eventconnect.dto.ReservationStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
 * @version 2.0.2
 */
@Service
@Transactional
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;
    private final EvenementService evenementService;

    public ReservationService(ReservationRepository reservationRepository,
                              UtilisateurRepository utilisateurRepository,
                              EvenementRepository evenementRepository,
                              EvenementService evenementService) {
        this.reservationRepository = reservationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.evenementRepository = evenementRepository;
        this.evenementService = evenementService;
    }

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
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérification de l'existence de l'événement
        Evenement evenement = evenementRepository.findById(evenementId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Vérification que l'événement est dans le futur
        if (evenement.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de réserver un événement passé");
        }

        // Vérification qu'il n'existe pas déjà une réservation
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
        BigDecimal montantTotal = evenement.getPrix().multiply(new BigDecimal(nombrePlaces));

        // Création de la réservation
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setEvenement(evenement);
        reservation.setNombrePlaces(nombrePlaces);
        reservation.setMontantTotal(montantTotal);
        reservation.setStatut(Reservation.StatutReservation.EN_ATTENTE);
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
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getStatut() != Reservation.StatutReservation.EN_ATTENTE) {
            throw new RuntimeException("Seules les réservations en attente peuvent être confirmées");
        }

        // Vérification que l'événement est toujours dans le futur
        if (reservation.getEvenement().getDateDebut().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Impossible de confirmer une réservation pour un événement passé");
        }

        reservation.setStatut(Reservation.StatutReservation.CONFIRMEE);
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
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.annuler(); // Appelle la méthode de l'entité qui libère les places
        Reservation reservationAnnulee = reservationRepository.save(reservation);
        log.info("Réservation annulée avec succès, ID: {}", reservationAnnulee.getId());

        return reservationAnnulee;
    }

    /**
     * Met à jour une réservation
     * @param id l'ID de la réservation
     * @param nombrePlaces le nouveau nombre de places
     * @param commentaire le nouveau commentaire
     * @return la réservation mise à jour
     * @throws RuntimeException si la réservation ne peut pas être mise à jour
     */
    public Reservation mettreAJourReservation(Long id, Integer nombrePlaces, String commentaire) {
        log.info("Mise à jour de la réservation ID: {}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (!reservation.isModifiable()) {
            throw new RuntimeException("La réservation n'est pas modifiable");
        }

        // Vérifier la disponibilité des places
        int placesDejaReservees = reservation.getNombrePlaces();
        int placesSupplementaires = nombrePlaces - placesDejaReservees;

        if (placesSupplementaires > 0 && !evenementService.verifierDisponibilite(reservation.getEvenement().getId(), placesSupplementaires)) {
            throw new RuntimeException("Pas assez de places disponibles pour cet événement");
        }

        // Mettre à jour les champs
        reservation.setNombrePlaces(nombrePlaces);
        reservation.setCommentaire(commentaire);
        reservation.setMontantTotal(reservation.getEvenement().getPrix().multiply(new BigDecimal(nombrePlaces)));
        reservation.setDateModification(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    /**
     * Rembourse une réservation
     * @param reservationId l'ID de la réservation
     * @return la réservation remboursée
     * @throws RuntimeException si la réservation ne peut pas être remboursée
     */
    public Reservation rembourserReservation(Long reservationId) {
        log.info("Remboursement de la réservation ID: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.marquerCommeRemboursee();
        return reservationRepository.save(reservation);
    }

    /**
     * Recherche une réservation par son ID
     * @param id l'ID de la réservation
     * @return la réservation si elle existe
     */
    @Transactional(readOnly = true)
    public Optional<Reservation> trouverParId(Long id) {
        log.info("Recherche de la réservation ID: {}", id);
        return reservationRepository.findByIdWithDetails(id);
    }

    /**
     * Récupère toutes les réservations
     * @param pageable paramètres de pagination
     * @return liste des réservations actives
     */
    @Transactional(readOnly = true)
    public List<Reservation> obtenirToutesReservations(Pageable pageable) {
        log.info("Récupération de toutes les réservations");
        return reservationRepository.findByActifTrue(pageable);
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
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

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
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        return reservationRepository.findByEvenement(evenement);
    }

    /**
     * Récupère les réservations par statut
     * @param statut le statut recherché
     * @return liste des réservations avec ce statut
     */
    @Transactional(readOnly = true)
    public List<Reservation> obtenirReservationsParStatut(Reservation.StatutReservation statut) {
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
            throw new RuntimeException("Réservation non trouvée");
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
        return reservationRepository.countByActifTrue();
    }

    /**
     * Calcule les statistiques des réservations
     * @return les statistiques des réservations
     */
    @Transactional(readOnly = true)
    public ReservationStats calculerStatistiques() {
        log.info("Calcul des statistiques des réservations");

        ReservationStats stats = new ReservationStats();

        // Calcul des totaux par statut
        stats.setTotalReservations(reservationRepository.countByActifTrue());
        stats.setReservationsConfirmees(reservationRepository.countByStatutAndActifTrue(Reservation.StatutReservation.CONFIRMEE));
        stats.setReservationsEnAttente(reservationRepository.countByStatutAndActifTrue(Reservation.StatutReservation.EN_ATTENTE));
        stats.setReservationsAnnulees(reservationRepository.countByStatutAndActifTrue(Reservation.StatutReservation.ANNULEE));

        // Calcul du chiffre d'affaires total
        BigDecimal chiffreAffairesTotal = reservationRepository.calculateChiffredAffairesPeriode(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now()
        );
        stats.setChiffreAffairesTotal(chiffreAffairesTotal != null ? chiffreAffairesTotal : BigDecimal.ZERO);

        // Calcul du taux de confirmation
        stats.setTauxConfirmation(stats.getTotalReservations() > 0 ?
                (double) stats.getReservationsConfirmees() / stats.getTotalReservations() * 100 : 0.0);

        return stats;
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