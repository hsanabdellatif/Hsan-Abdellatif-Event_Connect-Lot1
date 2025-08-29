package com.eventconnect.repositories;

import com.eventconnect.entities.EnqueteSatisfaction;
import com.eventconnect.entities.Evenement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des enquêtes de satisfaction
 * Fournit les méthodes d'accès aux données pour les enquêtes post-événement
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@Repository
public interface EnqueteSatisfactionRepository extends JpaRepository<EnqueteSatisfaction, Long> {

    /**
     * Trouve l'enquête active pour un événement
     * 
     * @param evenement l'événement
     * @return l'enquête optionnelle
     */
    @Query("SELECT e FROM EnqueteSatisfaction e WHERE e.evenement = :evenement " +
           "AND e.active = true " +
           "AND (e.dateExpiration IS NULL OR e.dateExpiration > :now) " +
           "ORDER BY e.dateCreation DESC")
    Optional<EnqueteSatisfaction> findActiveByEvenement(@Param("evenement") Evenement evenement,
                                                        @Param("now") LocalDateTime now);

    /**
     * Trouve toutes les enquêtes d'un événement
     * 
     * @param evenement l'événement
     * @return liste des enquêtes
     */
    List<EnqueteSatisfaction> findByEvenementOrderByDateCreationDesc(Evenement evenement);

    /**
     * Trouve les enquêtes actives
     * 
     * @param pageable pagination
     * @return page d'enquêtes actives
     */
    @Query("SELECT e FROM EnqueteSatisfaction e WHERE e.active = true " +
           "AND (e.dateExpiration IS NULL OR e.dateExpiration > :now) " +
           "ORDER BY e.dateCreation DESC")
    Page<EnqueteSatisfaction> findEnquetesActives(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Trouve les enquêtes expirées
     * 
     * @param pageable pagination
     * @return page d'enquêtes expirées
     */
    @Query("SELECT e FROM EnqueteSatisfaction e WHERE e.dateExpiration IS NOT NULL " +
           "AND e.dateExpiration <= :now " +
           "ORDER BY e.dateExpiration DESC")
    Page<EnqueteSatisfaction> findEnquetesExpirees(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Compte les enquêtes d'un événement
     * 
     * @param evenement l'événement
     * @return le nombre d'enquêtes
     */
    Long countByEvenement(Evenement evenement);

    /**
     * Trouve les enquêtes obligatoires actives
     * 
     * @return liste des enquêtes obligatoires
     */
    @Query("SELECT e FROM EnqueteSatisfaction e WHERE e.obligatoire = true " +
           "AND e.active = true " +
           "AND (e.dateExpiration IS NULL OR e.dateExpiration > :now) " +
           "ORDER BY e.dateCreation DESC")
    List<EnqueteSatisfaction> findEnquetesObligatoiresActives(@Param("now") LocalDateTime now);

    /**
     * Trouve les enquêtes créées dans une période
     * 
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @param pageable pagination
     * @return page d'enquêtes
     */
    @Query("SELECT e FROM EnqueteSatisfaction e WHERE e.dateCreation BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateCreation DESC")
    Page<EnqueteSatisfaction> findByDateCreationBetween(@Param("dateDebut") LocalDateTime dateDebut,
                                                        @Param("dateFin") LocalDateTime dateFin,
                                                        Pageable pageable);
}
