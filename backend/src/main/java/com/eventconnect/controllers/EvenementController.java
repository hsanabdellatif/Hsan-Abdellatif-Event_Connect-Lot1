package com.eventconnect.controllers;

import com.eventconnect.entities.Evenement;
import com.eventconnect.services.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des événements
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EvenementController {

    private final EvenementService evenementService;

    /**
     * Crée un nouvel événement
     * @param evenement les données de l'événement à créer
     * @return l'événement créé
     */
    @PostMapping
    public ResponseEntity<Evenement> creerEvenement(@Valid @RequestBody Evenement evenement) {
        log.info("POST /api/evenements - Création d'un événement: {}", evenement.getTitre());
        
        try {
            Evenement nouvelEvenement = evenementService.creerEvenement(evenement);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelEvenement);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création de l'événement: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de l'événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère un événement par son ID
     * @param id l'ID de l'événement
     * @return l'événement trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Evenement> obtenirEvenement(@PathVariable Long id) {
        log.info("GET /api/evenements/{} - Récupération de l'événement", id);
        
        Optional<Evenement> evenement = evenementService.trouverParId(id);
        return evenement
            .map(e -> ResponseEntity.ok().body(e))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère tous les événements avec pagination
     * @param page numéro de la page (défaut: 0)
     * @param size taille de la page (défaut: 10)
     * @param sortBy champ de tri (défaut: dateDebut)
     * @param sortDir direction du tri (défaut: asc)
     * @return page d'événements
     */
    @GetMapping
    public ResponseEntity<Page<Evenement>> obtenirTousLesEvenements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateDebut") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("GET /api/evenements - Récupération des événements (page: {}, taille: {})", page, size);
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Evenement> evenements = evenementService.obtenirTousLesEvenements(pageable);
            
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des événements", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Met à jour un événement
     * @param id l'ID de l'événement à mettre à jour
     * @param evenement les nouvelles données de l'événement
     * @return l'événement mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Evenement> mettreAJourEvenement(
            @PathVariable Long id, 
            @Valid @RequestBody Evenement evenement) {
        log.info("PUT /api/evenements/{} - Mise à jour de l'événement", id);
        
        try {
            Evenement evenementMisAJour = evenementService.mettreAJourEvenement(id, evenement);
            return ResponseEntity.ok(evenementMisAJour);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de l'événement: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour de l'événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Supprime un événement
     * @param id l'ID de l'événement à supprimer
     * @return réponse de suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEvenement(@PathVariable Long id) {
        log.info("DELETE /api/evenements/{} - Suppression de l'événement", id);
        
        try {
            evenementService.supprimerEvenement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de l'événement: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de l'événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recherche des événements par titre
     * @param titre le titre à rechercher
     * @return liste des événements correspondants
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Evenement>> rechercherParTitre(@RequestParam String titre) {
        log.info("GET /api/evenements/recherche?titre={} - Recherche par titre", titre);
        
        try {
            List<Evenement> evenements = evenementService.rechercherParTitre(titre);
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche par titre", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les événements futurs
     * @return liste des événements futurs
     */
    @GetMapping("/futurs")
    public ResponseEntity<List<Evenement>> obtenirEvenementsFuturs() {
        log.info("GET /api/evenements/futurs - Récupération des événements futurs");
        
        try {
            List<Evenement> evenements = evenementService.obtenirEvenementsFuturs();
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des événements futurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les événements par catégorie
     * @param categorie la catégorie recherchée
     * @return liste des événements de la catégorie
     */
    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<Evenement>> obtenirEvenementsParCategorie(@PathVariable String categorie) {
        log.info("GET /api/evenements/categorie/{} - Récupération par catégorie", categorie);
        
        try {
            List<Evenement> evenements = evenementService.obtenirEvenementsParCategorie(categorie);
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération par catégorie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les événements disponibles (avec des places libres)
     * @return liste des événements disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Evenement>> obtenirEvenementsDisponibles() {
        log.info("GET /api/evenements/disponibles - Récupération des événements disponibles");
        
        try {
            List<Evenement> evenements = evenementService.obtenirEvenementsDisponibles();
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des événements disponibles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifie la disponibilité d'un événement
     * @param id l'ID de l'événement
     * @param nombrePlaces le nombre de places demandées
     * @return true si des places sont disponibles
     */
    @GetMapping("/{id}/disponibilite")
    public ResponseEntity<Boolean> verifierDisponibilite(
            @PathVariable Long id, 
            @RequestParam Integer nombrePlaces) {
        log.info("GET /api/evenements/{}/disponibilite?nombrePlaces={}", id, nombrePlaces);
        
        try {
            boolean disponible = evenementService.verifierDisponibilite(id, nombrePlaces);
            return ResponseEntity.ok(disponible);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la vérification de disponibilité: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la vérification de disponibilité", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Compte le nombre total d'événements
     * @return le nombre d'événements
     */
    @GetMapping("/count")
    public ResponseEntity<Long> compterEvenements() {
        log.info("GET /api/evenements/count - Comptage des événements");
        
        try {
            long nombreEvenements = evenementService.compterEvenements();
            return ResponseEntity.ok(nombreEvenements);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des événements", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
