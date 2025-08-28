package com.eventconnect.repositories;

import com.eventconnect.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité Role
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Trouve un rôle par son nom
     */
    Optional<Role> findByNom(String nom);

    /**
     * Vérifie si un rôle existe par son nom
     */
    boolean existsByNom(String nom);

    /**
     * Trouve tous les rôles actifs
     */
    @Query("SELECT r FROM Role r WHERE r.actif = true")
    Iterable<Role> findAllActive();
}
