package com.eventconnect.repositories;

import com.eventconnect.entities.Evenement;
import com.eventconnect.entities.Utilisateur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'entité Evenement
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    /**
     * Trouve tous les événements actifs
     * @return Liste des événements actifs
     */
    List<Evenement> findByActifTrue();

    /**
     * Trouve tous les événements par statut
     * @param statut le statut recherché
     * @return Liste des événements avec ce statut
     */
    List<Evenement> findByStatut(Evenement.StatutEvenement statut);

    /**
     * Trouve tous les événements actifs par statut
     * @param statut le statut recherché
     * @return Liste des événements actifs avec ce statut
     */
    List<Evenement> findByStatutAndActifTrue(Evenement.StatutEvenement statut);

    /**
     * Trouve tous les événements par catégorie
     * @param categorie la catégorie recherchée
     * @return Liste des événements de cette catégorie
     */
    List<Evenement> findByCategorie(Evenement.CategorieEvenement categorie);

    /**
     * Trouve tous les événements actifs par catégorie
     * @param categorie la catégorie recherchée
     * @return Liste des événements actifs de cette catégorie
     */
    List<Evenement> findByCategorieAndActifTrue(Evenement.CategorieEvenement categorie);

    /**
     * Trouve tous les événements d'un organisateur
     * @param organisateur l'organisateur
     * @return Liste des événements organisés par cet utilisateur
     */
    List<Evenement> findByOrganisateur(Utilisateur organisateur);

    /**
     * Trouve tous les événements actifs d'un organisateur
     * @param organisateur l'organisateur
     * @return Liste des événements actifs organisés par cet utilisateur
     */
    List<Evenement> findByOrganisateurAndActifTrue(Utilisateur organisateur);

    /**
     * Trouve les événements à venir (date de début dans le futur)
     * @return Liste des événements à venir
     */
    @Query("SELECT e FROM Evenement e WHERE e.dateDebut > :now AND e.actif = true")
    List<Evenement> findEvenementsAvenir(@Param("now") LocalDateTime now);

    /**
     * Trouve les événements passés (date de fin dans le passé)
     * @return Liste des événements passés
     */
    @Query("SELECT e FROM Evenement e WHERE e.dateFin < :now AND e.actif = true")
    List<Evenement> findEvenementsPasses(@Param("now") LocalDateTime now);

    /**
     * Trouve les événements en cours (entre date de début et date de fin)
     * @return Liste des événements en cours
     */
    @Query("SELECT e FROM Evenement e WHERE :now BETWEEN e.dateDebut AND e.dateFin AND e.actif = true")
    List<Evenement> findEvenementsEnCours(@Param("now") LocalDateTime now);

    /**
     * Trouve les événements avec des places disponibles
     * @return Liste des événements avec places disponibles
     */
    @Query("SELECT e FROM Evenement e WHERE e.placesDisponibles > 0 AND e.actif = true")
    List<Evenement> findEvenementsAvecPlacesDisponibles();

    /**
     * Trouve les événements complets (plus de places)
     * @return Liste des événements complets
     */
    @Query("SELECT e FROM Evenement e WHERE e.placesDisponibles = 0 AND e.actif = true")
    List<Evenement> findEvenementsComplets();

    /**
     * Trouve les événements gratuits
     * @return Liste des événements gratuits
     */
    @Query("SELECT e FROM Evenement e WHERE e.prix = 0 AND e.actif = true")
    List<Evenement> findEvenementsGratuits();

    /**
     * Trouve les événements payants
     * @return Liste des événements payants
     */
    @Query("SELECT e FROM Evenement e WHERE e.prix > 0 AND e.actif = true")
    List<Evenement> findEvenementsPayants();

    /**
     * Trouve les événements dans une fourchette de prix
     * @param prixMin prix minimum
     * @param prixMax prix maximum
     * @return Liste des événements dans cette fourchette de prix
     */
    @Query("SELECT e FROM Evenement e WHERE e.prix BETWEEN :prixMin AND :prixMax AND e.actif = true")
    List<Evenement> findEvenementsByPrixBetween(@Param("prixMin") BigDecimal prixMin,
                                                @Param("prixMax") BigDecimal prixMax);

    /**
     * Trouve les événements dans une ville (recherche insensible à la casse)
     * @param lieu la ville ou lieu
     * @return Liste des événements dans cette ville
     */
    @Query("SELECT e FROM Evenement e WHERE LOWER(e.lieu) LIKE LOWER(CONCAT('%', :lieu, '%')) AND e.actif = true")
    List<Evenement> findByLieuContainingIgnoreCase(@Param("lieu") String lieu);

    /**
     * Recherche textuelle complète sur titre, description et lieu
     * @param searchTerm terme de recherche
     * @return Liste des événements correspondants
     */
    @Query("SELECT e FROM Evenement e WHERE e.actif = true AND " +
            "(LOWER(e.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.lieu) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Evenement> rechercherEvenements(@Param("searchTerm") String searchTerm);

    /**
     * Trouve les événements dans une période donnée
     * @param dateDebut date de début de la période
     * @param dateFin date de fin de la période
     * @return Liste des événements dans cette période
     */
    @Query("SELECT e FROM Evenement e WHERE " +
            "(e.dateDebut BETWEEN :dateDebut AND :dateFin OR " +
            "e.dateFin BETWEEN :dateDebut AND :dateFin OR " +
            "(e.dateDebut <= :dateDebut AND e.dateFin >= :dateFin)) AND e.actif = true")
    List<Evenement> findEvenementsDansPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                              @Param("dateFin") LocalDateTime dateFin);

    /**
     * Compte le nombre d'événements actifs
     * @return nombre d'événements actifs
     */
    long countByActifTrue();

    /**
     * Compte le nombre d'événements par statut
     * @param statut le statut
     * @return nombre d'événements avec ce statut
     */
    long countByStatutAndActifTrue(Evenement.StatutEvenement statut);

    /**
     * Trouve les événements les plus populaires (avec le plus de réservations)
     * @param limit nombre maximum d'événements à retourner
     * @return Liste des événements les plus populaires
     */
    @Query("SELECT e FROM Evenement e " +
            "LEFT JOIN Reservation r ON e.id = r.evenement.id AND r.actif = true " +
            "WHERE e.actif = true " +
            "GROUP BY e " +
            "ORDER BY COUNT(r) DESC")
    List<Evenement> findEvenementsLesplusPopulaires(Pageable pageable);

    /**
     * Trouve les événements par titre (recherche partielle)
     * @param titre le titre à rechercher
     * @return Liste des événements correspondants
     */
    List<Evenement> findByTitreContaining(String titre);

    /**
     * Trouve les événements futurs (après une date donnée)
     * @param date la date de référence
     * @return Liste des événements futurs
     */
    List<Evenement> findByDateDebutGreaterThan(LocalDateTime date);

    /**
     * Trouve les événements d'un organisateur dans une période donnée
     * @param organisateur l'organisateur
     * @param dateDebut date de début de la période
     * @param dateFin date de fin de la période
     * @return Liste des événements ordonnés par date de début
     */
    List<Evenement> findByOrganisateurAndDateDebutBetweenOrderByDateDebut(
            Utilisateur organisateur, LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Trouve les événements en conflit avec une période donnée pour un organisateur
     * @param organisateur l'organisateur
     * @param dateDebut date de début de la période à vérifier
     * @param dateFin date de fin de la période à vérifier
     * @return Liste des événements en conflit
     */
    @Query("SELECT e FROM Evenement e WHERE e.organisateur = :organisateur " +
            "AND e.actif = true " +
            "AND ((e.dateDebut BETWEEN :dateDebut AND :dateFin) " +
            "OR (e.dateFin BETWEEN :dateDebut AND :dateFin) " +
            "OR (e.dateDebut <= :dateDebut AND e.dateFin >= :dateFin)) " +
            "ORDER BY e.dateDebut")
    List<Evenement> findEvenementsConflitPourOrganisateur(
            @Param("organisateur") Utilisateur organisateur,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    /**
     * Compte les places réservées pour un événement
     * @param evenementId l'ID de l'événement
     * @return le nombre de places réservées
     */
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r " +
            "WHERE r.evenement.id = :evenementId AND r.statut = 'CONFIRMEE' AND r.actif = true")
    Integer countPlacesReserveesParEvenement(@Param("evenementId") Long evenementId);
}