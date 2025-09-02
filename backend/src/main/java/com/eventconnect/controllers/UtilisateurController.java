package com.eventconnect.controllers;

import com.eventconnect.entities.Utilisateur;
import com.eventconnect.services.UtilisateurService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des utilisateurs
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private static final Logger log = LoggerFactory.getLogger(UtilisateurController.class);
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Crée un nouvel utilisateur
     * @param utilisateur les données de l'utilisateur à créer
     * @return l'utilisateur créé
     */
    @PostMapping
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        log.info("POST /utilisateurs - Création d'un utilisateur avec email: {}", utilisateur.getEmail());

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
        log.info("GET /utilisateurs/{} - Récupération de l'utilisateur", id);

        try {
            Utilisateur utilisateur = utilisateurService.obtenirUtilisateurParId(id);
            return ResponseEntity.ok().body(utilisateur);
        } catch (RuntimeException e) {
            log.error("Utilisateur non trouvé avec l'ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Récupère tous les utilisateurs
     * @return la liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<Utilisateur>> obtenirTousLesUtilisateurs() {
        log.info("GET /utilisateurs - Récupération de tous les utilisateurs");

        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateursActifs();
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
        log.info("PUT /utilisateurs/{} - Mise à jour de l'utilisateur", id);

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
        log.info("DELETE /utilisateurs/{} - Suppression de l'utilisateur", id);

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
        log.info("GET /utilisateurs/recherche?email={} - Recherche par email", email);

        try {
            Optional<Utilisateur> utilisateur = utilisateurService.obtenirUtilisateurParEmail(email);
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
        log.info("GET /utilisateurs/count - Comptage des utilisateurs");

        try {
            long nombreUtilisateurs = utilisateurService.compterUtilisateursActifs();
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
        log.info("GET /utilisateurs/exists?email={} - Vérification d'existence", email);

        try {
            boolean existe = utilisateurService.existsByEmail(email);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'existence", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Added to match frontend
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("GET /utilisateurs/stats - Récupération des statistiques des utilisateurs");
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", utilisateurService.compterUtilisateursActifs());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<Utilisateur>> getActiveUsers() {
        log.info("GET /utilisateurs/actifs - Récupération des utilisateurs actifs");
        try {
            List<Utilisateur> activeUsers = utilisateurService.obtenirTousLesUtilisateursActifs();
            return ResponseEntity.ok(activeUsers);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des utilisateurs actifs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Utilisateur>> searchUsers(@RequestParam String q) {
        log.info("GET /utilisateurs/search?q={} - Recherche des utilisateurs", q);
        try {
            List<Utilisateur> users = utilisateurService.rechercherUtilisateurs(q);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Utilisateur> getUserByEmail(@PathVariable String email) {
        log.info("GET /utilisateurs/email/{} - Récupération de l'utilisateur par email", email);
        try {
            Optional<Utilisateur> user = utilisateurService.obtenirUtilisateurParEmail(email);
            return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur par email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Utilisateur> toggleUserStatus(@PathVariable Long id) {
        log.info("PATCH /utilisateurs/{}/toggle-status - Toggle statut de l'utilisateur", id);
        try {
            Utilisateur user = utilisateurService.obtenirUtilisateurParId(id);
            boolean newStatus = !user.getActif();
            Utilisateur updatedUser = utilisateurService.changerStatutUtilisateur(id, newStatus);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("Erreur lors du toggle statut: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors du toggle statut", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}