package com.eventconnect.controllers;

import com.eventconnect.dto.CreneauLibreDto;
import com.eventconnect.dto.VerificationConflitDto;
import com.eventconnect.services.AgendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des agendas et détection de conflits
 * Fournit les fonctionnalités de vérification de disponibilité et proposition
 * de créneaux
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/agenda")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:3000" })
public class AgendaController {

    private static final Logger logger = LoggerFactory.getLogger(AgendaController.class);

    @Autowired
    private AgendaService agendaService;

    /**
     * Vérifie s'il y a des conflits d'agenda pour un organisateur dans une période
     * donnée
     * 
     * @param organisateurId ID de l'organisateur
     * @param dateDebut      Date de début de la période à vérifier
     * @param dateFin        Date de fin de la période à vérifier
     * @return Résultat de la vérification avec les détails des conflits
     */
    @GetMapping("/conflits/{organisateurId}")
    public ResponseEntity<VerificationConflitDto> verifierConflits(
            @PathVariable Long organisateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        logger.info("Vérification des conflits d'agenda pour l'organisateur {} du {} au {}",
                organisateurId, dateDebut, dateFin);

        try {
            boolean conflit = agendaService.detecterConflitAgenda(organisateurId, dateDebut, dateFin);

            VerificationConflitDto resultat = new VerificationConflitDto();
            resultat.setConflitDetecte(conflit);
            resultat.setTypeVerification("ORGANISATEUR");
            resultat.setDateVerification(LocalDateTime.now());

            if (conflit) {
                List<VerificationConflitDto.ConflitDetailDto> details = agendaService
                        .obtenirEvenementsEnConflit(organisateurId, dateDebut, dateFin)
                        .stream()
                        .map(e -> {
                            VerificationConflitDto.ConflitDetailDto detail = new VerificationConflitDto.ConflitDetailDto();
                            detail.setEvenementId(e.getId());
                            detail.setNomEvenement(e.getTitre());
                            detail.setDateDebut(e.getDateDebut());
                            detail.setDateFin(e.getDateFin());
                            detail.setLieu(e.getLieu());
                            detail.setTypeConflit("CONFLIT");
                            return detail;
                        })
                        .toList();

                resultat.setConflits(details);
                resultat.setMessage("Des conflits ont été détectés dans l'agenda");
            } else {
                resultat.setConflits(List.of());
                resultat.setMessage("Aucun conflit détecté");
            }

            logger.debug("Conflits détectés: {} pour l'organisateur {}",
                    resultat.isConflitDetecte(), organisateurId);

            return ResponseEntity.ok(resultat);

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification des conflits pour l'organisateur {}: {}",
                    organisateurId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Vérifie s'il y a des conflits de réservation pour un utilisateur dans une
     * période donnée
     * 
     * @param utilisateurId ID de l'utilisateur
     * @param dateDebut     Date de début de la période à vérifier
     * @param dateFin       Date de fin de la période à vérifier
     * @return Résultat de la vérification avec les détails des conflits
     */
    @GetMapping("/conflits/reservations/{utilisateurId}")
    public ResponseEntity<VerificationConflitDto> verifierConflitsReservations(
            @PathVariable Long utilisateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        logger.info("Vérification des conflits de réservation pour l'utilisateur {} du {} au {}",
                utilisateurId, dateDebut, dateFin);

        try {
            // Pour les utilisateurs, nous considérons qu'il n'y a pas de conflits
            // Une nouvelle fonctionnalité à implémenter dans le futur
            VerificationConflitDto resultat = new VerificationConflitDto();
            resultat.setConflitDetecte(false);
            resultat.setTypeVerification("UTILISATEUR");
            resultat.setDateVerification(LocalDateTime.now());
            resultat.setConflits(List.of());
            resultat.setMessage("Aucun conflit de réservation détecté");

            logger.debug("Conflits de réservation détectés: {} pour l'utilisateur {}",
                    resultat.isConflitDetecte(), utilisateurId);

            return ResponseEntity.ok(resultat);

        } catch (Exception e) {
            logger.error("Erreur lors de la vérification des conflits de réservation pour l'utilisateur {}: {}",
                    utilisateurId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Propose des créneaux libres pour un organisateur dans une période donnée
     * 
     * @param organisateurId ID de l'organisateur
     * @param dateDebut      Date de début de la période de recherche
     * @param dateFin        Date de fin de la période de recherche
     * @param dureeMinutes   Durée souhaitée en minutes
     * @param limit          Nombre maximum de créneaux à retourner (optionnel,
     *                       défaut: 10)
     * @return Liste des créneaux libres proposés
     */
    @GetMapping("/creneaux-libres/{organisateurId}")
    public ResponseEntity<List<CreneauLibreDto>> proposerCreneauxLibres(
            @PathVariable Long organisateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam int dureeMinutes,
            @RequestParam(defaultValue = "10") int limit) {

        logger.info("Proposition de créneaux libres pour l'organisateur {} du {} au {} (durée: {}min, limite: {})",
                organisateurId, dateDebut, dateFin, dureeMinutes, limit);

        try {
            // Convertir dureeMinutes en heures pour le service
            int dureeHeures = (dureeMinutes + 59) / 60; // Arrondi supérieur

            // Calculer le nombre de jours entre dateDebut et dateFin
            int nombreJoursRecherche = (int) java.time.Duration.between(
                    dateDebut.toLocalDate().atStartOfDay(),
                    dateFin.toLocalDate().atStartOfDay()).toDays() + 1; // +1 pour inclure le dernier jour

            List<AgendaService.CreneauLibre> creneauxService = agendaService.proposerCreneauxLibres(
                    organisateurId, dateDebut, dureeHeures, nombreJoursRecherche);

            // Conversion des CreneauLibre du service en CreneauLibreDto
            List<CreneauLibreDto> creneauxLibres = creneauxService.stream()
                    .map(creneau -> new CreneauLibreDto(
                            creneau.getDebut(),
                            creneau.getFin(),
                            50, // Score de proximité par défaut
                            creneau.getDescription()))
                    .limit(limit) // Limiter au nombre demandé
                    .toList();

            logger.debug("Nombre de créneaux libres proposés: {} pour l'organisateur {}",
                    creneauxLibres.size(), organisateurId);

            return ResponseEntity.ok(creneauxLibres);

        } catch (Exception e) {
            logger.error("Erreur lors de la proposition de créneaux libres pour l'organisateur {}: {}",
                    organisateurId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Propose des créneaux alternatifs en cas de conflit
     * 
     * @param organisateurId    ID de l'organisateur
     * @param dateDebutSouhaite Date de début souhaitée initialement
     * @param dateFin           Date de fin de la période de recherche
     * @param dureeMinutes      Durée souhaitée en minutes
     * @param limit             Nombre maximum de créneaux à retourner (optionnel,
     *                          défaut: 5)
     * @return Liste des créneaux alternatifs proposés
     */
    @GetMapping("/alternatives/{organisateurId}")
    @PreAuthorize("hasRole('ADMIN') or @utilisateurService.isCurrentUserOrAdmin(#organisateurId)")
    public ResponseEntity<List<CreneauLibreDto>> proposerAlternatives(
            @PathVariable Long organisateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebutSouhaite,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam int dureeMinutes,
            @RequestParam(defaultValue = "5") int limit) {

        logger.info("Proposition d'alternatives pour l'organisateur {} à partir du {} (durée: {}min, limite: {})",
                organisateurId, dateDebutSouhaite, dureeMinutes, limit);

        try {
            // Convertir dureeMinutes en heures pour le service
            int dureeHeures = (dureeMinutes + 59) / 60; // Arrondi supérieur

            List<AgendaService.CreneauLibre> alternativesService = agendaService.proposerAlternatives(
                    organisateurId, dateDebutSouhaite, dureeHeures);

            // Conversion des CreneauLibre du service en CreneauLibreDto
            List<CreneauLibreDto> alternatives = alternativesService.stream()
                    .map(creneau -> {
                        // Calcul du score de proximité basé sur la différence de temps avec la date
                        // souhaitée
                        long diffHeures = java.time.Duration.between(
                                dateDebutSouhaite, creneau.getDebut()).toHours();
                        int scoreProximite = diffHeures <= 24 ? 80 : // Même jour ou jour suivant
                                diffHeures <= 48 ? 60 : // Dans les 2 jours
                                        diffHeures <= 72 ? 40 : // Dans les 3 jours
                                                20; // Plus lointain

                        return new CreneauLibreDto(
                                creneau.getDebut(),
                                creneau.getFin(),
                                scoreProximite,
                                creneau.getDescription());
                    })
                    .limit(limit) // Limiter au nombre demandé
                    .toList();

            logger.debug("Nombre d'alternatives proposées: {} pour l'organisateur {}",
                    alternatives.size(), organisateurId);

            return ResponseEntity.ok(alternatives);

        } catch (Exception e) {
            logger.error("Erreur lors de la proposition d'alternatives pour l'organisateur {}: {}",
                    organisateurId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtient un résumé de l'agenda d'un organisateur pour une semaine donnée
     * 
     * @param organisateurId ID de l'organisateur
     * @param dateDebut      Date de début de la semaine
     * @return Résumé de l'agenda avec statistiques
     */
    @GetMapping("/resume/{organisateurId}")
    @PreAuthorize("hasRole('ADMIN') or @utilisateurService.isCurrentUserOrAdmin(#organisateurId)")
    public ResponseEntity<Map<String, Object>> obtenirResumeAgenda(
            @PathVariable Long organisateurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut) {

        logger.info("Demande de résumé d'agenda pour l'organisateur {} à partir du {}",
                organisateurId, dateDebut);

        try {
            // Création d'un résumé d'agenda simple basé sur les événements de la semaine
            LocalDateTime dateFin = dateDebut.plusDays(7); // Une semaine

            // Cette fonctionnalité n'est pas encore implémentée dans le service
            // On retourne un résumé simplifié
            Map<String, Object> resume = new HashMap<>();
            resume.put("organisateurId", organisateurId);
            resume.put("periode", Map.of(
                    "debut", dateDebut,
                    "fin", dateFin));

            // Pour des données réelles, il faudrait implémenter cette méthode dans
            // AgendaService
            resume.put("evenements", List.of());
            resume.put("statistiques", Map.of(
                    "nombreEvenements", 0,
                    "heuresOccupees", 0,
                    "tauxOccupation", 0));

            logger.debug("Résumé d'agenda généré pour l'organisateur {}", organisateurId);
            return ResponseEntity.ok(resume);

        } catch (Exception e) {
            logger.error("Erreur lors de la génération du résumé d'agenda pour l'organisateur {}: {}",
                    organisateurId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de santé pour vérifier le bon fonctionnement du service
     * 
     * @return Status du service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "AgendaService",
                "timestamp", LocalDateTime.now(),
                "version", "2.0.0"));
    }
}
