package com.eventconnect.repositories;

import com.eventconnect.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Trouve un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur s'il existe
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs actifs
     * @return Liste des utilisateurs actifs
     */
    List<Utilisateur> findByActifTrue();

    /**
     * Trouve tous les utilisateurs par rôle
     * @param role le rôle recherché
     * @return Liste des utilisateurs avec ce rôle
     */
    List<Utilisateur> findByRole(Utilisateur.RoleUtilisateur role);

    /**
     * Trouve tous les utilisateurs actifs par rôle
     * @param role le rôle recherché
     * @return Liste des utilisateurs actifs avec ce rôle
     */
    List<Utilisateur> findByRoleAndActifTrue(Utilisateur.RoleUtilisateur role);

    /**
     * Recherche des utilisateurs par nom ou prénom (insensible à la casse)
     * @param nom le nom à rechercher
     * @param prenom le prénom à rechercher
     * @return Liste des utilisateurs correspondants
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :nom, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :prenom, '%'))")
    List<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            @Param("nom") String nom,
            @Param("prenom") String prenom);

    /**
     * Trouve tous les organisateurs actifs
     * @return Liste des organisateurs actifs
     */
    @Query("SELECT u FROM Utilisateur u WHERE " +
            "(u.role = 'ORGANISATEUR' OR u.role = 'ADMIN') AND u.actif = true")
    List<Utilisateur> findAllOrganisateursActifs();

    /**
     * Compte le nombre total d'utilisateurs actifs
     * @return nombre d'utilisateurs actifs
     */
    long countByActifTrue();

    /**
     * Trouve les utilisateurs qui ont organisé au moins un événement
     * @return Liste des utilisateurs organisateurs
     */
    @Query("SELECT DISTINCT u FROM Utilisateur u " +
            "INNER JOIN u.evenementsOrganises e " +
            "WHERE u.actif = true")
    List<Utilisateur> findUtilisateursAvecEvenements();

    /**
     * Trouve les utilisateurs qui ont fait au moins une réservation
     * @return Liste des utilisateurs avec réservations
     */
    @Query("SELECT DISTINCT u FROM Utilisateur u " +
            "INNER JOIN u.reservations r " +
            "WHERE u.actif = true AND r.actif = true")
    List<Utilisateur> findUtilisateursAvecReservations();

    /**
     * Recherche textuelle complète sur nom, prénom et email
     * @param searchTerm terme de recherche
     * @return Liste des utilisateurs correspondants
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.actif = true AND " +
            "(LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Utilisateur> rechercherUtilisateurs(@Param("searchTerm") String searchTerm);
}