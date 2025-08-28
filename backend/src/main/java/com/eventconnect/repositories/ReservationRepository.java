package com.eventconnect.repositories;

import com.eventconnect.entities.Evenement;
import com.eventconnect.entities.Reservation;
import com.eventconnect.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Reservation
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Trouve toutes les réservations actives
     * @return Liste des réservations actives
     */
    List<Reservation> findByActifTrue();

    /**
     * Trouve toutes les réservations par statut
     * @param statut le statut recherché
     * @return Liste des réservations avec ce statut
     */
    List<Reservation> findByStatut(Reservation.StatutReservation statut);

    /**
     * Trouve toutes les réservations actives par statut
     * @param statut le statut recherché
     * @return Liste des réservations actives avec ce statut
     */
    List<Reservation> findByStatutAndActifTrue(Reservation.StatutReservation statut);

    /**
     * Trouve toutes les réservations d'un utilisateur
     * @param utilisateur l'utilisateur
     * @return Liste des réservations de cet utilisateur
     */
    List<Reservation> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve toutes les réservations actives d'un utilisateur
     * @param utilisateur l'utilisateur
     * @return Liste des réservations actives de cet utilisateur
     */
    List<Reservation> findByUtilisateurAndActifTrue(Utilisateur utilisateur);

    /**
     * Trouve toutes les réservations pour un événement
     * @param evenement l'événement
     * @return Liste des réservations pour cet événement
     */
    List<Reservation> findByEvenement(Evenement evenement);

    /**
     * Trouve toutes les réservations actives pour un événement
     * @param evenement l'événement
     * @return Liste des réservations actives pour cet événement
     */
    List<Reservation> findByEvenementAndActifTrue(Evenement evenement);

    /**
     * Trouve une réservation par utilisateur et événement
     * @param utilisateur l'utilisateur
     * @param evenement l'événement
     * @return Optional contenant la réservation si elle existe
     */
    Optional<Reservation> findByUtilisateurAndEvenement(Utilisateur utilisateur, Evenement evenement);

    /**
     * Trouve une réservation par son code
     * @param codeReservation le code de la réservation
     * @return Optional contenant la réservation si elle existe
     */
    Optional<Reservation> findByCodeReservation(String codeReservation);

    /**
     * Vérifie si une réservation existe pour un utilisateur et un événement
     * @param utilisateur l'utilisateur
     * @param evenement l'événement
     * @return true si une réservation existe, false sinon
     */
    boolean existsByUtilisateurAndEvenementAndActifTrue(Utilisateur utilisateur, Evenement evenement);

    /**
     * Trouve les réservations confirmées d'un utilisateur
     * @param utilisateur l'utilisateur
     * @return Liste des réservations confirmées
     */
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur = :utilisateur " +
           "AND r.statut = 'CONFIRMEE' AND r.actif = true")
    List<Reservation> findReservationsConfirmeesUtilisateur(@Param("utilisateur") Utilisateur utilisateur);

    /**
     * Trouve les réservations en attente de confirmation
     * @return Liste des réservations en attente
     */
    @Query("SELECT r FROM Reservation r WHERE r.statut = 'EN_ATTENTE' AND r.actif = true")
    List<Reservation> findReservationsEnAttente();

    /**
     * Trouve les réservations expirées (en attente depuis plus de X heures)
     * @param dateExpiration date limite pour considérer une réservation comme expirée
     * @return Liste des réservations expirées
     */
    @Query("SELECT r FROM Reservation r WHERE r.statut = 'EN_ATTENTE' " +
           "AND r.dateReservation < :dateExpiration AND r.actif = true")
    List<Reservation> findReservationsExpirees(@Param("dateExpiration") LocalDateTime dateExpiration);

    /**
     * Calcule le nombre total de places réservées pour un événement
     * @param evenement l'événement
     * @return nombre total de places réservées
     */
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r " +
           "WHERE r.evenement = :evenement AND r.statut IN ('EN_ATTENTE', 'CONFIRMEE') AND r.actif = true")
    Integer countPlacesReserveesEvenement(@Param("evenement") Evenement evenement);

    /**
     * Calcule le chiffre d'affaires total pour un événement
     * @param evenement l'événement
     * @return montant total des réservations confirmées
     */
    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r " +
           "WHERE r.evenement = :evenement AND r.statut = 'CONFIRMEE' AND r.actif = true")
    BigDecimal calculateChiffredAffairesEvenement(@Param("evenement") Evenement evenement);

    /**
     * Trouve les réservations d'un utilisateur pour les événements à venir
     * @param utilisateur l'utilisateur
     * @param dateActuelle date actuelle
     * @return Liste des réservations pour événements futurs
     */
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur = :utilisateur " +
           "AND r.evenement.dateDebut > :dateActuelle AND r.actif = true " +
           "AND r.statut IN ('EN_ATTENTE', 'CONFIRMEE')")
    List<Reservation> findReservationsEvenementsFuturs(@Param("utilisateur") Utilisateur utilisateur,
                                                       @Param("dateActuelle") LocalDateTime dateActuelle);

    /**
     * Trouve les réservations d'un utilisateur pour les événements passés
     * @param utilisateur l'utilisateur
     * @param dateActuelle date actuelle
     * @return Liste des réservations pour événements passés
     */
    @Query("SELECT r FROM Reservation r WHERE r.utilisateur = :utilisateur " +
           "AND r.evenement.dateFin < :dateActuelle AND r.actif = true")
    List<Reservation> findReservationsEvenementsPasses(@Param("utilisateur") Utilisateur utilisateur,
                                                       @Param("dateActuelle") LocalDateTime dateActuelle);

    /**
     * Trouve les réservations dans une période donnée
     * @param dateDebut date de début de la période
     * @param dateFin date de fin de la période
     * @return Liste des réservations dans cette période
     */
    @Query("SELECT r FROM Reservation r WHERE r.dateReservation BETWEEN :dateDebut AND :dateFin " +
           "AND r.actif = true")
    List<Reservation> findReservationsDansPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                                  @Param("dateFin") LocalDateTime dateFin);

    /**
     * Compte le nombre total de réservations actives
     * @return nombre de réservations actives
     */
    long countByActifTrue();

    /**
     * Compte le nombre de réservations par statut
     * @param statut le statut
     * @return nombre de réservations avec ce statut
     */
    long countByStatutAndActifTrue(Reservation.StatutReservation statut);

    /**
     * Calcule le chiffre d'affaires total sur une période
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return chiffre d'affaires total
     */
    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r " +
           "WHERE r.dateConfirmation BETWEEN :dateDebut AND :dateFin " +
           "AND r.statut = 'CONFIRMEE' AND r.actif = true")
    BigDecimal calculateChiffredAffairesPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                               @Param("dateFin") LocalDateTime dateFin);

    /**
     * Trouve les utilisateurs les plus actifs (avec le plus de réservations)
     * @param limit nombre maximum d'utilisateurs à retourner
     * @return Liste des utilisateurs les plus actifs
     */
    @Query(value = "SELECT u.* FROM utilisateurs u " +
                   "INNER JOIN reservations r ON u.id = r.utilisateur_id " +
                   "WHERE r.actif = true AND r.statut = 'CONFIRMEE' " +
                   "GROUP BY u.id " +
                   "ORDER BY COUNT(r.id) DESC " +
                   "LIMIT :limit", nativeQuery = true)
    List<Utilisateur> findUtilisateursLesPlusActifs(@Param("limit") int limit);

    /**
     * Trouve les réservations qui peuvent être remboursées
     * @param dateMinimum date minimum pour le remboursement (24h avant l'événement)
     * @return Liste des réservations remboursables
     */
    @Query("SELECT r FROM Reservation r WHERE r.statut = 'CONFIRMEE' " +
           "AND r.evenement.dateDebut > :dateMinimum AND r.actif = true")
    List<Reservation> findReservationsRemboursables(@Param("dateMinimum") LocalDateTime dateMinimum);

    /**
     * Calcule le chiffre d'affaires total pour un événement
     * @param evenementId l'ID de l'événement
     * @return le chiffre d'affaires total
     */
    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r " +
           "WHERE r.evenement.id = :evenementId AND r.statut = 'CONFIRMEE' AND r.actif = true")
    BigDecimal calculateTotalRevenueForEvenement(@Param("evenementId") Long evenementId);

    /**
     * Compte le nombre de places réservées pour un événement
     * @param evenementId l'ID de l'événement
     * @return le nombre de places réservées
     */
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r " +
           "WHERE r.evenement.id = :evenementId AND r.statut = 'CONFIRMEE' AND r.actif = true")
    Integer countPlacesReserveesForEvenement(@Param("evenementId") Long evenementId);
}
