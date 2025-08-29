package com.eventconnect.repositories;

import com.eventconnect.entities.Badge;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.entities.UtilisateurBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité UtilisateurBadge
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Repository
public interface UtilisateurBadgeRepository extends JpaRepository<UtilisateurBadge, Long> {

    /**
     * Trouve tous les badges d'un utilisateur
     * @param utilisateur l'utilisateur
     * @return Liste des badges de l'utilisateur
     */
    List<UtilisateurBadge> findByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve tous les badges d'un utilisateur ordonnés par date d'obtention
     * @param utilisateur l'utilisateur
     * @return Liste des badges ordonnée
     */
    List<UtilisateurBadge> findByUtilisateurOrderByDateObtentionDesc(Utilisateur utilisateur);

    /**
     * Vérifie si un utilisateur possède un badge spécifique
     * @param utilisateur l'utilisateur
     * @param badge le badge
     * @return true si l'utilisateur possède le badge
     */
    boolean existsByUtilisateurAndBadge(Utilisateur utilisateur, Badge badge);

    /**
     * Trouve un badge spécifique d'un utilisateur
     * @param utilisateur l'utilisateur
     * @param badge le badge
     * @return Optional du UtilisateurBadge
     */
    Optional<UtilisateurBadge> findByUtilisateurAndBadge(Utilisateur utilisateur, Badge badge);

    /**
     * Compte le nombre de badges d'un utilisateur
     * @param utilisateur l'utilisateur
     * @return le nombre de badges
     */
    long countByUtilisateur(Utilisateur utilisateur);

    /**
     * Trouve les utilisateurs qui ont obtenu un badge spécifique
     * @param badge le badge
     * @return Liste des UtilisateurBadge
     */
    List<UtilisateurBadge> findByBadgeOrderByDateObtentionDesc(Badge badge);

    /**
     * Trouve les badges les plus récemment obtenus
     * @param limit nombre maximum de résultats
     * @return Liste des badges récents
     */
    @Query("SELECT ub FROM UtilisateurBadge ub ORDER BY ub.dateObtention DESC")
    List<UtilisateurBadge> findBadgesRecents();

    /**
     * Trouve les badges d'un type spécifique pour un utilisateur
     * @param utilisateur l'utilisateur
     * @param type le type de badge
     * @return Liste des badges du type spécifié
     */
    @Query("SELECT ub FROM UtilisateurBadge ub WHERE ub.utilisateur = :utilisateur " +
           "AND ub.badge.type = :type ORDER BY ub.dateObtention DESC")
    List<UtilisateurBadge> findByUtilisateurAndBadgeType(@Param("utilisateur") Utilisateur utilisateur,
                                                         @Param("type") Badge.TypeBadge type);
}
