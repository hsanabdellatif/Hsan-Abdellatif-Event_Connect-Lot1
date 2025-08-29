package com.eventconnect.controllers;

import com.eventconnect.entities.Badge;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.entities.UtilisateurBadge;
import com.eventconnect.services.FideliteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion du système de fidélité et des badges
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/fidelite")
@CrossOrigin(origins = "*")
public class FideliteController {

    private static final Logger log = LoggerFactory.getLogger(FideliteController.class);
    private final FideliteService fideliteService;

    public FideliteController(FideliteService fideliteService) {
        this.fideliteService = fideliteService;
    }

    /**
     * Obtient le profil de fidélité d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return le profil de fidélité
     */
    @GetMapping("/profil/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or #utilisateurId == authentication.principal.id")
    public ResponseEntity<Utilisateur> obtenirProfilFidelite(@PathVariable Long utilisateurId) {
        log.info("GET /api/fidelite/profil/{} - Récupération du profil de fidélité", utilisateurId);
        
        try {
            Utilisateur profil = fideliteService.obtenirProfilFidelite(utilisateurId);
            return ResponseEntity.ok(profil);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération du profil de fidélité: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération du profil de fidélité", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtient tous les badges d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return la liste des badges de l'utilisateur
     */
    @GetMapping("/badges/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or #utilisateurId == authentication.principal.id")
    public ResponseEntity<List<UtilisateurBadge>> obtenirBadgesUtilisateur(@PathVariable Long utilisateurId) {
        log.info("GET /api/fidelite/badges/{} - Récupération des badges de l'utilisateur", utilisateurId);
        
        try {
            List<UtilisateurBadge> badges = fideliteService.obtenirBadgesUtilisateur(utilisateurId);
            return ResponseEntity.ok(badges);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des badges: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des badges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtient les prochains badges qu'un utilisateur peut obtenir
     * @param utilisateurId l'ID de l'utilisateur
     * @return la liste des prochains badges atteignables
     */
    @GetMapping("/prochains-badges/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or #utilisateurId == authentication.principal.id")
    public ResponseEntity<List<Badge>> obtenirProchainsBadges(@PathVariable Long utilisateurId) {
        log.info("GET /api/fidelite/prochains-badges/{} - Récupération des prochains badges", utilisateurId);
        
        try {
            List<Badge> prochainsBadges = fideliteService.obtenirProchainsBadges(utilisateurId);
            return ResponseEntity.ok(prochainsBadges);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récupération des prochains badges: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des prochains badges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Attribue des points à un utilisateur (Admin uniquement)
     * @param utilisateurId l'ID de l'utilisateur
     * @param points le nombre de points à ajouter
     * @return la liste des nouveaux badges obtenus
     */
    @PostMapping("/attribuer-points/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Badge>> attribuerPoints(
            @PathVariable Long utilisateurId,
            @RequestParam Integer points) {
        log.info("POST /api/fidelite/attribuer-points/{} - Attribution de {} points", utilisateurId, points);
        
        try {
            if (points <= 0) {
                return ResponseEntity.badRequest().build();
            }
            
            List<Badge> nouveauxBadges = fideliteService.attribuerPoints(utilisateurId, points);
            return ResponseEntity.ok(nouveauxBadges);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'attribution des points: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'attribution des points", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtient tous les badges disponibles
     * @return la liste de tous les badges
     */
    @GetMapping("/badges")
    public ResponseEntity<List<Badge>> obtenirTousLesBadges() {
        log.info("GET /api/fidelite/badges - Récupération de tous les badges");
        
        try {
            List<Badge> badges = fideliteService.obtenirTousLesBadges();
            return ResponseEntity.ok(badges);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des badges", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour récompenser une réservation (usage interne)
     * @param utilisateurId l'ID de l'utilisateur
     * @return réponse de succès
     */
    @PostMapping("/recompenser-reservation/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANISATEUR')")
    public ResponseEntity<String> recompenserReservation(@PathVariable Long utilisateurId) {
        log.info("POST /api/fidelite/recompenser-reservation/{} - Récompense pour réservation", utilisateurId);
        
        try {
            fideliteService.recompenserReservation(utilisateurId);
            return ResponseEntity.ok("Récompense attribuée avec succès");
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récompense de réservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récompense de réservation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint pour récompenser l'organisation d'un événement (usage interne)
     * @param utilisateurId l'ID de l'utilisateur organisateur
     * @return réponse de succès
     */
    @PostMapping("/recompenser-organisation/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANISATEUR')")
    public ResponseEntity<String> recompenserOrganisation(@PathVariable Long utilisateurId) {
        log.info("POST /api/fidelite/recompenser-organisation/{} - Récompense pour organisation", utilisateurId);
        
        try {
            fideliteService.recompenserOrganisation(utilisateurId);
            return ResponseEntity.ok("Récompense d'organisation attribuée avec succès");
        } catch (RuntimeException e) {
            log.error("Erreur lors de la récompense d'organisation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récompense d'organisation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
