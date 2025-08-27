package com.eventconnect.controllers;

import com.eventconnect.entities.Utilisateur;
import com.eventconnect.services.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des utilisateurs
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    /**
     * Crée un nouvel utilisateur
     * @param utilisateur les données de l'utilisateur à créer
     * @return l'utilisateur créé
     */
    @PostMapping
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        log.info("POST /api/utilisateurs - Création d'un utilisateur avec email: {}", utilisateur.getEmail());
        
        try {
            Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateur(utilisateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateur(@PathVariable Long id) {
        log.info("GET /api/utilisateurs/{} - Récupération de l'utilisateur", id);
        
        Optional<Utilisateur> utilisateur = utilisateurService.trouverParId(id);
        return utilisateur
            .map(u -> ResponseEntity.ok().body(u))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère tous les utilisateurs
     * @return la liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<Utilisateur>> obtenirTousLesUtilisateurs() {
        log.info("GET /api/utilisateurs - Récupération de tous les utilisateurs");
        
        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateurs();
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Met à jour un utilisateur
     * @param id l'ID de l'utilisateur à mettre à jour
     * @param utilisateur les nouvelles données de l'utilisateur
     * @return l'utilisateur mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(
            @PathVariable Long id, 
            @Valid @RequestBody Utilisateur utilisateur) {
        log.info("PUT /api/utilisateurs/{} - Mise à jour de l'utilisateur", id);
        
        try {
            Utilisateur utilisateurMisAJour = utilisateurService.mettreAJourUtilisateur(id, utilisateur);
            return ResponseEntity.ok(utilisateurMisAJour);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime un utilisateur
     * @param id l'ID de l'utilisateur à supprimer
     * @return réponse de suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        log.info("DELETE /api/utilisateurs/{} - Suppression de l'utilisateur", id);
        
        try {
            utilisateurService.supprimerUtilisateur(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recherche des utilisateurs par email
     * @param email l'email à rechercher
     * @return l'utilisateur trouvé
     */
    @GetMapping("/recherche")
    public ResponseEntity<Utilisateur> rechercherParEmail(@RequestParam String email) {
        log.info("GET /api/utilisateurs/recherche?email={} - Recherche par email", email);
        
        try {
            Optional<Utilisateur> utilisateur = utilisateurService.trouverParEmail(email);
            return utilisateur
                .map(u -> ResponseEntity.ok().body(u))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erreur lors de la recherche par email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Compte le nombre total d'utilisateurs
     * @return le nombre d'utilisateurs
     */
    @GetMapping("/count")
    public ResponseEntity<Long> compterUtilisateurs() {
        log.info("GET /api/utilisateurs/count - Comptage des utilisateurs");
        
        try {
            long nombreUtilisateurs = utilisateurService.compterUtilisateurs();
            return ResponseEntity.ok(nombreUtilisateurs);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> verifierExistenceEmail(@RequestParam String email) {
        log.info("GET /api/utilisateurs/exists?email={} - Vérification d'existence", email);
        
        try {
            boolean existe = utilisateurService.existeParEmail(email);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'existence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
