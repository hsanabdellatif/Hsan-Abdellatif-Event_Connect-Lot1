package com.eventconnect.services;

import com.eventconnect.entities.Utilisateur;
import com.eventconnect.repositories.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs
 * Implémente UserDetailsService pour l'authentification Spring Security
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Service
@Transactional
public class UtilisateurService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Charge un utilisateur par son email pour Spring Security
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
            .username(utilisateur.getEmail())
            .password(utilisateur.getMotDePasse())
            .authorities(new ArrayList<>()) // Pour l'instant, pas de rôles spécifiques
            .build();
    }

    /**
     * Trouve un utilisateur par email
     */
    @Transactional(readOnly = true)
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'email: " + email));
    }

    /**
     * Vérifie si un email existe déjà
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Crée un nouvel utilisateur (méthode publique pour AuthController)
     */
    public Utilisateur creer(Utilisateur utilisateur) {
        return creerUtilisateur(utilisateur);
    }

    /**
     * Crée un nouvel utilisateur
     * @param utilisateur l'utilisateur à créer
     * @return l'utilisateur créé
     * @throws IllegalArgumentException si l'email existe déjà
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        log.info("Création d'un nouvel utilisateur avec l'email: {}", utilisateur.getEmail());
        
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        // TODO: Hasher le mot de passe avant sauvegarde
        Utilisateur nouvelUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur créé avec succès, ID: {}", nouvelUtilisateur.getId());
        
        return nouvelUtilisateur;
    }

    /**
     * Met à jour un utilisateur existant
     * @param id l'ID de l'utilisateur
     * @param utilisateurMiseAJour les données de mise à jour
     * @return l'utilisateur mis à jour
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    public Utilisateur mettreAJourUtilisateur(Long id, Utilisateur utilisateurMiseAJour) {
        log.info("Mise à jour de l'utilisateur ID: {}", id);
        
        Utilisateur utilisateurExistant = obtenirUtilisateurParId(id);
        
        // Vérifier si l'email a changé et s'il est déjà utilisé
        if (!utilisateurExistant.getEmail().equals(utilisateurMiseAJour.getEmail())) {
            if (utilisateurRepository.existsByEmail(utilisateurMiseAJour.getEmail())) {
                throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
            }
            utilisateurExistant.setEmail(utilisateurMiseAJour.getEmail());
        }
        
        // Mettre à jour les autres champs
        utilisateurExistant.setNom(utilisateurMiseAJour.getNom());
        utilisateurExistant.setPrenom(utilisateurMiseAJour.getPrenom());
        utilisateurExistant.setTelephone(utilisateurMiseAJour.getTelephone());
        
        // Ne pas permettre la modification du rôle via cette méthode
        // utilisateurExistant.setRole(utilisateurMiseAJour.getRole());
        
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateurExistant);
        log.info("Utilisateur mis à jour avec succès, ID: {}", id);
        
        return utilisateurSauvegarde;
    }

    /**
     * Supprime un utilisateur (suppression logique)
     * @param id l'ID de l'utilisateur à supprimer
     */
    public void supprimerUtilisateur(Long id) {
        log.info("Suppression de l'utilisateur ID: {}", id);
        
        Utilisateur utilisateur = obtenirUtilisateurParId(id);
        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
        
        log.info("Utilisateur supprimé avec succès, ID: {}", id);
    }

    /**
     * Obtient un utilisateur par son ID
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur trouvé
     * @throws RuntimeException si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public Utilisateur obtenirUtilisateurParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
    }

    /**
     * Obtient un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur s'il existe
     */
    @Transactional(readOnly = true)
    public Optional<Utilisateur> obtenirUtilisateurParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Obtient tous les utilisateurs actifs
     * @return liste des utilisateurs actifs
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirTousLesUtilisateursActifs() {
        return utilisateurRepository.findByActifTrue();
    }

    /**
     * Obtient tous les utilisateurs par rôle
     * @param role le rôle recherché
     * @return liste des utilisateurs avec ce rôle
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirUtilisateursParRole(Utilisateur.RoleUtilisateur role) {
        return utilisateurRepository.findByRoleAndActifTrue(role);
    }

    /**
     * Obtient tous les organisateurs actifs
     * @return liste des organisateurs actifs
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirOrganisateursActifs() {
        return utilisateurRepository.findAllOrganisateursActifs();
    }

    /**
     * Recherche des utilisateurs par terme de recherche
     * @param termesRecherche terme de recherche
     * @return liste des utilisateurs correspondants
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> rechercherUtilisateurs(String termesRecherche) {
        if (termesRecherche == null || termesRecherche.trim().isEmpty()) {
            return obtenirTousLesUtilisateursActifs();
        }
        return utilisateurRepository.rechercherUtilisateurs(termesRecherche.trim());
    }

    /**
     * Change le rôle d'un utilisateur (admin uniquement)
     * @param id l'ID de l'utilisateur
     * @param nouveauRole le nouveau rôle
     * @return l'utilisateur mis à jour
     */
    public Utilisateur changerRoleUtilisateur(Long id, Utilisateur.RoleUtilisateur nouveauRole) {
        log.info("Changement de rôle pour l'utilisateur ID: {} vers {}", id, nouveauRole);
        
        Utilisateur utilisateur = obtenirUtilisateurParId(id);
        utilisateur.setRole(nouveauRole);
        
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        log.info("Rôle changé avec succès pour l'utilisateur ID: {}", id);
        
        return utilisateurSauvegarde;
    }

    /**
     * Active ou désactive un utilisateur
     * @param id l'ID de l'utilisateur
     * @param actif true pour activer, false pour désactiver
     * @return l'utilisateur mis à jour
     */
    public Utilisateur changerStatutUtilisateur(Long id, boolean actif) {
        log.info("Changement de statut pour l'utilisateur ID: {} vers {}", id, actif ? "actif" : "inactif");
        
        Utilisateur utilisateur = obtenirUtilisateurParId(id);
        utilisateur.setActif(actif);
        
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
        log.info("Statut changé avec succès pour l'utilisateur ID: {}", id);
        
        return utilisateurSauvegarde;
    }

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe, false sinon
     */
    @Transactional(readOnly = true)
    public boolean emailExiste(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Obtient le nombre total d'utilisateurs actifs
     * @return nombre d'utilisateurs actifs
     */
    @Transactional(readOnly = true)
    public long compterUtilisateursActifs() {
        return utilisateurRepository.countByActifTrue();
    }

    /**
     * Obtient les utilisateurs qui ont organisé des événements
     * @return liste des utilisateurs organisateurs
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirUtilisateursAvecEvenements() {
        return utilisateurRepository.findUtilisateursAvecEvenements();
    }

    /**
     * Obtient les utilisateurs qui ont fait des réservations
     * @return liste des utilisateurs avec réservations
     */
    @Transactional(readOnly = true)
    public List<Utilisateur> obtenirUtilisateursAvecReservations() {
        return utilisateurRepository.findUtilisateursAvecReservations();
    }
}
