package com.eventconnect.repositories;

import com.eventconnect.entities.Evaluation;
import com.eventconnect.entities.Evenement;
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
 * Repository pour la gestion des évaluations d'événements
 * Fournit les méthodes d'accès aux données pour les feedbacks et notations
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    /**
     * Trouve une évaluation par utilisateur et événement
     * 
     * @param utilisateur l'utilisateur
     * @param evenement l'événement
     * @return l'évaluation optionnelle
     */
    Optional<Evaluation> findByUtilisateurAndEvenement(Utilisateur utilisateur, Evenement evenement);

    /**
     * Vérifie si un utilisateur a déjà évalué un événement
     * 
     * @param utilisateur l'utilisateur
     * @param evenement l'événement
     * @return true si une évaluation existe
     */
    boolean existsByUtilisateurAndEvenement(Utilisateur utilisateur, Evenement evenement);

    /**
     * Trouve toutes les évaluations d'un événement (visibles)
     * 
     * @param evenement l'événement
     * @param pageable pagination
     * @return page d'évaluations
     */
    @Query("SELECT e FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.visible = true AND e.actif = true " +
           "ORDER BY e.dateEvaluation DESC")
    Page<Evaluation> findByEvenementVisible(@Param("evenement") Evenement evenement, Pageable pageable);

    /**
     * Trouve les évaluations d'un utilisateur
     * 
     * @param utilisateur l'utilisateur
     * @param pageable pagination
     * @return page d'évaluations
     */
    @Query("SELECT e FROM Evaluation e WHERE e.utilisateur = :utilisateur " +
           "AND e.actif = true " +
           "ORDER BY e.dateEvaluation DESC")
    Page<Evaluation> findByUtilisateur(@Param("utilisateur") Utilisateur utilisateur, Pageable pageable);

    /**
     * Calcule la note moyenne d'un événement
     * 
     * @param evenement l'événement
     * @return la note moyenne
     */
    @Query("SELECT AVG(e.note) FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.visible = true AND e.actif = true")
    Double calculateAverageNoteByEvenement(@Param("evenement") Evenement evenement);

    /**
     * Compte le nombre d'évaluations d'un événement
     * 
     * @param evenement l'événement
     * @return le nombre d'évaluations
     */
    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.visible = true AND e.actif = true")
    Long countByEvenementVisible(@Param("evenement") Evenement evenement);

    /**
     * Trouve les évaluations par note
     * 
     * @param evenement l'événement
     * @param note la note recherchée
     * @return liste des évaluations
     */
    @Query("SELECT e FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.note = :note AND e.visible = true AND e.actif = true " +
           "ORDER BY e.dateEvaluation DESC")
    List<Evaluation> findByEvenementAndNote(@Param("evenement") Evenement evenement, 
                                           @Param("note") Integer note);

    /**
     * Trouve les évaluations recommandées pour un événement
     * 
     * @param evenement l'événement
     * @param pageable pagination
     * @return page d'évaluations recommandées
     */
    @Query("SELECT e FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.recommande = true AND e.visible = true AND e.actif = true " +
           "ORDER BY e.utile DESC, e.dateEvaluation DESC")
    Page<Evaluation> findRecommandeesByEvenement(@Param("evenement") Evenement evenement, Pageable pageable);

    /**
     * Trouve les évaluations nécessitant une modération
     * 
     * @param pageable pagination
     * @return page d'évaluations à modérer
     */
    @Query("SELECT e FROM Evaluation e WHERE e.actif = true " +
           "AND (e.modere = false AND (e.signale >= 2 OR e.note <= 2)) " +
           "ORDER BY e.signale DESC, e.dateEvaluation ASC")
    Page<Evaluation> findEvaluationsAModerer(Pageable pageable);

    /**
     * Trouve les meilleures évaluations (les plus utiles)
     * 
     * @param evenement l'événement
     * @param limit nombre maximum d'évaluations
     * @return liste des meilleures évaluations
     */
    @Query("SELECT e FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.visible = true AND e.actif = true " +
           "ORDER BY e.utile DESC, e.note DESC, e.dateEvaluation DESC")
    List<Evaluation> findTopEvaluationsByEvenement(@Param("evenement") Evenement evenement, 
                                                   Pageable pageable);

    /**
     * Calcule les statistiques d'évaluations pour un événement
     * 
     * @param evenement l'événement
     * @return objet avec les statistiques
     */
    @Query("SELECT " +
           "COUNT(e) as total, " +
           "AVG(e.note) as moyenne, " +
           "SUM(CASE WHEN e.note = 5 THEN 1 ELSE 0 END) as cinqEtoiles, " +
           "SUM(CASE WHEN e.note = 4 THEN 1 ELSE 0 END) as quatreEtoiles, " +
           "SUM(CASE WHEN e.note = 3 THEN 1 ELSE 0 END) as troisEtoiles, " +
           "SUM(CASE WHEN e.note = 2 THEN 1 ELSE 0 END) as deuxEtoiles, " +
           "SUM(CASE WHEN e.note = 1 THEN 1 ELSE 0 END) as uneEtoile, " +
           "SUM(CASE WHEN e.recommande = true THEN 1 ELSE 0 END) as recommandations " +
           "FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.visible = true AND e.actif = true")
    Object[] getStatistiquesEvaluations(@Param("evenement") Evenement evenement);

    /**
     * Trouve les évaluations récentes
     * 
     * @param dateDebut date à partir de laquelle chercher
     * @param pageable pagination
     * @return page d'évaluations récentes
     */
    @Query("SELECT e FROM Evaluation e WHERE e.dateEvaluation >= :dateDebut " +
           "AND e.visible = true AND e.actif = true " +
           "ORDER BY e.dateEvaluation DESC")
    Page<Evaluation> findEvaluationsRecentes(@Param("dateDebut") LocalDateTime dateDebut, 
                                            Pageable pageable);

    /**
     * Trouve les évaluations avec commentaires non vides
     * 
     * @param evenement l'événement
     * @param pageable pagination
     * @return page d'évaluations avec commentaires
     */
    @Query("SELECT e FROM Evaluation e WHERE e.evenement = :evenement " +
           "AND e.commentaire IS NOT NULL AND LENGTH(e.commentaire) > 0 " +
           "AND e.visible = true AND e.actif = true " +
           "ORDER BY e.dateEvaluation DESC")
    Page<Evaluation> findEvaluationsAvecCommentaires(@Param("evenement") Evenement evenement, 
                                                     Pageable pageable);

    /**
     * Compte les évaluations d'un utilisateur
     * 
     * @param utilisateur l'utilisateur
     * @return le nombre d'évaluations
     */
    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.utilisateur = :utilisateur AND e.actif = true")
    Long countByUtilisateur(@Param("utilisateur") Utilisateur utilisateur);
}
