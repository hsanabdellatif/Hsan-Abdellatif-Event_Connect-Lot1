package com.eventconnect.controllers;

import com.eventconnect.dto.EvenementDTO;
import com.eventconnect.entities.Evenement;
import com.eventconnect.services.EvenementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/evenements")
@CrossOrigin(origins = "*")
public class EvenementController {

    private static final Logger log = LoggerFactory.getLogger(EvenementController.class);
    private final EvenementService evenementService;
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("titre", "dateDebut", "dateFin", "prix", "lieu");

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    private static class ErrorResponse {
        private String message;
        private String details;
        private int status;

        public ErrorResponse(String message, String details, int status) {
            this.message = message;
            this.details = details;
            this.status = status;
        }

        public String getMessage() { return message; }
        public String getDetails() { return details; }
        public int getStatus() { return status; }
    }

    @PostMapping
    public ResponseEntity<?> creerEvenement(@Valid @RequestBody Evenement evenement) {
        log.info("POST /evenements - Création d'un événement: {}", evenement.getTitre());
        try {
            Evenement nouvelEvenement = evenementService.creerEvenement(evenement);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelEvenement);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création de l'événement: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation Error", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de l'événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenirTousLesEvenements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateDebut") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("GET /evenements - Récupération des événements (page: {}, taille: {})", page, size);
        try {
            if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
                throw new IllegalArgumentException("Champ de tri invalide: " + sortBy);
            }
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Evenement> evenements = evenementService.obtenirTousLesEvenements(pageable);
            return ResponseEntity.ok(evenements);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation Error", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des événements", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvenementById(@PathVariable Long id) {
        log.info("GET /evenements/{} - Récupération de l'événement", id);
        try {
            Optional<Evenement> evenement = evenementService.trouverParId(id);
            if (evenement.isEmpty()) {
                log.warn("Événement non trouvé pour l'ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Not Found", "Événement non trouvé pour l'ID: " + id, HttpStatus.NOT_FOUND.value()));
            }
            return ResponseEntity.ok(evenement.get());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'événement ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> mettreAJourEvenement(
            @PathVariable Long id,
            @Valid @RequestBody Evenement evenement) {
        log.info("PUT /evenements/{} - Mise à jour de l'événement", id);
        try {
            Evenement evenementMisAJour = evenementService.mettreAJourEvenement(id, evenement);
            return ResponseEntity.ok(evenementMisAJour);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation Error", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (RuntimeException e) {
            log.error("Erreur lors de la mise à jour de l'événement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la mise à jour de l'événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerEvenement(@PathVariable Long id) {
        log.info("DELETE /evenements/{} - Suppression de l'événement", id);
        try {
            evenementService.supprimerEvenement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur lors de la suppression de l'événement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression de l'événement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> rechercherParTitre(@RequestParam String q) {
        log.info("GET /evenements/search?q={} - Recherche par titre", q);
        try {
            List<Evenement> evenements = evenementService.rechercherParTitre(q);
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche par titre", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/futurs")
    public ResponseEntity<?> obtenirEvenementsFuturs() {
        log.info("GET /evenements/futurs - Récupération des événements futurs");
        try {
            List<Evenement> evenements = evenementService.obtenirEvenementsFuturs();
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des événements futurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<?> obtenirEvenementsParCategorie(@PathVariable String categorie) {
        log.info("GET /evenements/categorie/{} - Récupération par catégorie", categorie);
        try {
            List<Evenement> evenements = evenementService.obtenirEvenementsParCategorie(categorie);
            return ResponseEntity.ok(evenements);
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation Error", e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération par catégorie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> obtenirEvenementsDisponibles() {
        log.info("GET /evenements/disponibles - Récupération des événements disponibles");
        try {
            List<Evenement> evenements = evenementService.obtenirEvenementsDisponibles();
            return ResponseEntity.ok(evenements);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des événements disponibles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{id}/disponibilite")
    public ResponseEntity<?> verifierDisponibilite(
            @PathVariable Long id,
            @RequestParam Integer nombrePlaces) {
        log.info("GET /evenements/{}/disponibilite?nombrePlaces={}", id, nombrePlaces);
        try {
            boolean disponible = evenementService.verifierDisponibilite(id, nombrePlaces);
            return ResponseEntity.ok(disponible);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la vérification de disponibilité: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Not Found", e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la vérification de disponibilité", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> compterEvenements() {
        log.info("GET /evenements/count - Comptage des événements");
        try {
            long nombreEvenements = evenementService.compterEvenements();
            return ResponseEntity.ok(nombreEvenements);
        } catch (Exception e) {
            log.error("Erreur lors du comptage des événements", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        log.info("GET /evenements/stats - Récupération des statistiques des événements");
        try {
            long total = evenementService.compterEvenements();
            long evenementsFuturs = evenementService.obtenirEvenementsFuturs().size();
            long evenementsDisponibles = evenementService.obtenirEvenementsDisponibles().size();
            var stats = new HashMap<String, Object>();
            stats.put("total", total);
            stats.put("futurs", evenementsFuturs);
            stats.put("disponibles", evenementsDisponibles);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/actifs")
    public ResponseEntity<?> getEvenementsActifs() {
        log.info("GET /evenements/actifs - Récupération des événements actifs");
        try {
            List<EvenementDTO> evenementsActifs = evenementService.getEvenementsActifs();
            return ResponseEntity.ok(evenementsActifs);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des événements actifs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal Server Error", "Une erreur inattendue s'est produite: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}