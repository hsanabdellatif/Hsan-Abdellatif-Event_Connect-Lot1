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

    List<Evenement> findByActifTrue();

    List<Evenement> findByStatut(Evenement.StatutEvenement statut);

    List<Evenement> findByStatutAndActifTrue(Evenement.StatutEvenement statut);

    List<Evenement> findByCategorie(Evenement.CategorieEvenement categorie);

    List<Evenement> findByCategorieAndActifTrue(Evenement.CategorieEvenement categorie);

    List<Evenement> findByOrganisateur(Utilisateur organisateur);

    List<Evenement> findByOrganisateurAndActifTrue(Utilisateur organisateur);

    @Query("SELECT e FROM Evenement e WHERE e.dateDebut > :now AND e.actif = true")
    List<Evenement> findEvenementsAvenir(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Evenement e WHERE e.dateFin < :now AND e.actif = true")
    List<Evenement> findEvenementsPasses(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Evenement e WHERE :now BETWEEN e.dateDebut AND e.dateFin AND e.actif = true")
    List<Evenement> findEvenementsEnCours(@Param("now") LocalDateTime now);

    @Query("SELECT e FROM Evenement e WHERE e.placesDisponibles > 0 AND e.actif = true")
    List<Evenement> findEvenementsAvecPlacesDisponibles();

    @Query("SELECT e FROM Evenement e WHERE e.placesDisponibles = 0 AND e.actif = true")
    List<Evenement> findEvenementsComplets();

    @Query("SELECT e FROM Evenement e WHERE e.prix = 0 AND e.actif = true")
    List<Evenement> findEvenementsGratuits();

    @Query("SELECT e FROM Evenement e WHERE e.prix > 0 AND e.actif = true")
    List<Evenement> findEvenementsPayants();

    @Query("SELECT e FROM Evenement e WHERE e.prix BETWEEN :prixMin AND :prixMax AND e.actif = true")
    List<Evenement> findEvenementsByPrixBetween(@Param("prixMin") BigDecimal prixMin,
                                                @Param("prixMax") BigDecimal prixMax);

    @Query("SELECT e FROM Evenement e WHERE LOWER(e.lieu) LIKE LOWER(CONCAT('%', :lieu, '%')) AND e.actif = true")
    List<Evenement> findByLieuContainingIgnoreCase(@Param("lieu") String lieu);

    @Query("SELECT e FROM Evenement e WHERE e.actif = true AND " +
            "(LOWER(e.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(e.lieu) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Evenement> rechercherEvenements(@Param("searchTerm") String searchTerm);

    @Query("SELECT e FROM Evenement e WHERE LOWER(e.titre) LIKE LOWER(CONCAT('%', :titre, '%')) AND e.actif = true")
    List<Evenement> findByTitreContaining(@Param("titre") String titre);

    @Query("SELECT e FROM Evenement e WHERE " +
            "(e.dateDebut BETWEEN :dateDebut AND :dateFin OR " +
            "e.dateFin BETWEEN :dateDebut AND :dateFin OR " +
            "(e.dateDebut <= :dateDebut AND e.dateFin >= :dateFin)) AND e.actif = true")
    List<Evenement> findEvenementsDansPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                              @Param("dateFin") LocalDateTime dateFin);

    long countByActifTrue();

    long countByStatutAndActifTrue(Evenement.StatutEvenement statut);

    @Query("SELECT e FROM Evenement e " +
            "LEFT JOIN Reservation r ON e.id = r.evenement.id AND r.actif = true " +
            "WHERE e.actif = true " +
            "GROUP BY e " +
            "ORDER BY COUNT(r) DESC")
    List<Evenement> findEvenementsLesplusPopulaires(Pageable pageable);

    List<Evenement> findByDateDebutGreaterThan(LocalDateTime date);

    List<Evenement> findByOrganisateurAndDateDebutBetweenOrderByDateDebut(
            Utilisateur organisateur, LocalDateTime dateDebut, LocalDateTime dateFin);

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

    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r " +
            "WHERE r.evenement.id = :evenementId AND r.statut = 'CONFIRMEE' AND r.actif = true")
    Integer countPlacesReserveesParEvenement(@Param("evenementId") Long evenementId);
}