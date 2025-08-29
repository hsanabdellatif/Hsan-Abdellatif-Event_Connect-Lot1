package com.eventconnect.repositories;

import com.eventconnect.entities.ReponseEnquete;
import com.eventconnect.entities.EnqueteSatisfaction;
import com.eventconnect.entities.Utilisateur;
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
 * Repository pour la gestion des réponses aux enquêtes de satisfaction
 * Fournit les méthodes d'accès aux données pour les réponses utilisateur
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@Repository
public interface ReponseEnqueteRepository extends JpaRepository<ReponseEnquete, Long> {

    /**
     * Trouve une réponse par utilisateur et enquête
     * 
     * @param utilisateur l'utilisateur
     * @param enquete l'enquête
     * @return la réponse optionnelle
     */
    Optional<ReponseEnquete> findByUtilisateurAndEnquete(Utilisateur utilisateur, EnqueteSatisfaction enquete);

    /**
     * Vérifie si un utilisateur a déjà répondu à une enquête
     * 
     * @param utilisateur l'utilisateur
     * @param enquete l'enquête
     * @return true si une réponse existe
     */
    boolean existsByUtilisateurAndEnquete(Utilisateur utilisateur, EnqueteSatisfaction enquete);

    /**
     * Trouve toutes les réponses d'une enquête
     * 
     * @param enquete l'enquête
     * @param pageable pagination
     * @return page de réponses
     */
    @Query("SELECT r FROM ReponseEnquete r WHERE r.enquete = :enquete " +
           "ORDER BY r.dateReponse DESC")
    Page<ReponseEnquete> findByEnquete(@Param("enquete") EnqueteSatisfaction enquete, Pageable pageable);

    /**
     * Trouve les réponses complètes d'une enquête
     * 
     * @param enquete l'enquête
     * @param pageable pagination
     * @return page de réponses complètes
     */
    @Query("SELECT r FROM ReponseEnquete r WHERE r.enquete = :enquete " +
           "AND r.complete = true " +
           "ORDER BY r.dateReponse DESC")
    Page<ReponseEnquete> findReponsesCompletesByEnquete(@Param("enquete") EnqueteSatisfaction enquete, 
                                                        Pageable pageable);

    /**
     * Compte les réponses d'une enquête
     * 
     * @param enquete l'enquête
     * @return le nombre de réponses
     */
    Long countByEnquete(EnqueteSatisfaction enquete);

    /**
     * Compte les réponses complètes d'une enquête
     * 
     * @param enquete l'enquête
     * @return le nombre de réponses complètes
     */
    @Query("SELECT COUNT(r) FROM ReponseEnquete r WHERE r.enquete = :enquete AND r.complete = true")
    Long countReponsesCompletesByEnquete(@Param("enquete") EnqueteSatisfaction enquete);

    /**
     * Trouve les réponses d'un utilisateur
     * 
     * @param utilisateur l'utilisateur
     * @param pageable pagination
     * @return page de réponses
     */
    @Query("SELECT r FROM ReponseEnquete r WHERE r.utilisateur = :utilisateur " +
           "ORDER BY r.dateReponse DESC")
    Page<ReponseEnquete> findByUtilisateur(@Param("utilisateur") Utilisateur utilisateur, Pageable pageable);

    /**
     * Trouve les réponses anonymes d'une enquête
     * 
     * @param enquete l'enquête
     * @param pageable pagination
     * @return page de réponses anonymes
     */
    @Query("SELECT r FROM ReponseEnquete r WHERE r.enquete = :enquete " +
           "AND r.anonyme = true " +
           "ORDER BY r.dateReponse DESC")
    Page<ReponseEnquete> findReponsesAnonymesByEnquete(@Param("enquete") EnqueteSatisfaction enquete, 
                                                       Pageable pageable);

    /**
     * Trouve les réponses récentes
     * 
     * @param dateDebut date à partir de laquelle chercher
     * @param pageable pagination
     * @return page de réponses récentes
     */
    @Query("SELECT r FROM ReponseEnquete r WHERE r.dateReponse >= :dateDebut " +
           "ORDER BY r.dateReponse DESC")
    Page<ReponseEnquete> findReponsesRecentes(@Param("dateDebut") LocalDateTime dateDebut, 
                                             Pageable pageable);

    /**
     * Calcule le taux de complétion d'une enquête
     * 
     * @param enquete l'enquête
     * @return le pourcentage de complétion (0-100)
     */
    @Query("SELECT " +
           "CASE WHEN COUNT(r) = 0 THEN 0 " +
           "ELSE CAST(SUM(CASE WHEN r.complete = true THEN 1 ELSE 0 END) * 100.0 / COUNT(r) AS double) END " +
           "FROM ReponseEnquete r WHERE r.enquete = :enquete")
    Double calculateTauxCompletion(@Param("enquete") EnqueteSatisfaction enquete);

    /**
     * Trouve les réponses par adresse IP (pour détecter les doublons)
     * 
     * @param adresseIp l'adresse IP
     * @param enquete l'enquête
     * @return liste des réponses de cette IP
     */
    @Query("SELECT r FROM ReponseEnquete r WHERE r.adresseIp = :adresseIp " +
           "AND r.enquete = :enquete " +
           "ORDER BY r.dateReponse DESC")
    List<ReponseEnquete> findByAdresseIpAndEnquete(@Param("adresseIp") String adresseIp, 
                                                   @Param("enquete") EnqueteSatisfaction enquete);

    /**
     * Compte les réponses d'un utilisateur
     * 
     * @param utilisateur l'utilisateur
     * @return le nombre de réponses
     */
    Long countByUtilisateur(Utilisateur utilisateur);
}
