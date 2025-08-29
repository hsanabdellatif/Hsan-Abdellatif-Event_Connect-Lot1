package com.eventconnect.repositories;

import com.eventconnect.entities.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Badge
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    /**
     * Trouve tous les badges actifs
     * @return Liste des badges actifs
     */
    List<Badge> findByActifTrue();

    /**
     * Trouve les badges par type
     * @param type le type de badge
     * @return Liste des badges du type spécifié
     */
    List<Badge> findByTypeAndActifTrue(Badge.TypeBadge type);

    /**
     * Trouve un badge par son nom
     * @param nom le nom du badge
     * @return Optional contenant le badge s'il existe
     */
    Optional<Badge> findByNomAndActifTrue(String nom);

    /**
     * Trouve les badges qu'un utilisateur peut obtenir selon ses points
     * @param points les points de l'utilisateur
     * @return Liste des badges atteignables
     */
    @Query("SELECT b FROM Badge b WHERE b.pointsRequis <= :points AND b.actif = true " +
           "ORDER BY b.pointsRequis DESC")
    List<Badge> findBadgesAtteignables(@Param("points") Integer points);

    /**
     * Trouve le prochain badge à atteindre pour un utilisateur
     * @param points les points actuels de l'utilisateur
     * @return Optional du prochain badge
     */
    @Query("SELECT b FROM Badge b WHERE b.pointsRequis > :points AND b.actif = true " +
           "ORDER BY b.pointsRequis ASC")
    List<Badge> findProchainsBadges(@Param("points") Integer points);

    /**
     * Compte le nombre total de badges actifs
     * @return le nombre de badges actifs
     */
    long countByActifTrue();
}
