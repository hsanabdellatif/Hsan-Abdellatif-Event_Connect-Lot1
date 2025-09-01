package com.eventconnect.controllers;

import com.eventconnect.entities.Evaluation;
import com.eventconnect.entities.EnqueteSatisfaction;
import com.eventconnect.entities.ReponseEnquete;
import com.eventconnect.services.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des feedbacks et évaluations
 * Fournit les APIs pour le système de notation 5 étoiles et enquêtes de satisfaction
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@RestController
@RequestMapping("/feedbacks")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    // ================== ÉVALUATIONS ==================

    /**
     * Crée ou met à jour une évaluation d'événement
     * 
     * @param evaluationRequest requête d'évaluation
     * @return l'évaluation créée/mise à jour
     */
    @PostMapping("/evaluations")
    public ResponseEntity<Evaluation> evaluerEvenement(@Valid @RequestBody EvaluationRequest evaluationRequest) {
        logger.info("Demande d'évaluation événement: utilisateur={}, événement={}, note={}", 
                   evaluationRequest.getUtilisateurId(), evaluationRequest.getEvenementId(), 
                   evaluationRequest.getNote());

        try {
            Evaluation evaluation = feedbackService.evaluerEvenement(
                evaluationRequest.getUtilisateurId(),
                evaluationRequest.getEvenementId(),
                evaluationRequest.getNote(),
                evaluationRequest.getCommentaire(),
                evaluationRequest.getAnonyme()
            );

            logger.info("Évaluation créée/mise à jour avec succès ID: {}", evaluation.getId());
            return ResponseEntity.ok(evaluation);

        } catch (Exception e) {
            logger.error("Erreur lors de l'évaluation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère les évaluations d'un événement
     * 
     * @param evenementId ID de l'événement
     * @param page numéro de page (défaut: 0)
     * @param size taille de page (défaut: 10)
     * @return page d'évaluations
     */
    @GetMapping("/evaluations/evenement/{evenementId}")
    public ResponseEntity<Page<Evaluation>> obtenirEvaluationsEvenement(
            @PathVariable Long evenementId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Récupération évaluations événement {}: page={}, size={}", evenementId, page, size);

        try {
            Page<Evaluation> evaluations = feedbackService.obtenirEvaluationsEvenement(evenementId, page, size);
            
            logger.debug("Évaluations récupérées: {} éléments sur {} total", 
                        evaluations.getNumberOfElements(), evaluations.getTotalElements());
            
            return ResponseEntity.ok(evaluations);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des évaluations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtient les statistiques d'évaluation pour un événement
     * 
     * @param evenementId ID de l'événement
     * @return statistiques d'évaluation
     */
    @GetMapping("/evaluations/statistiques/{evenementId}")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesEvaluations(@PathVariable Long evenementId) {
        logger.info("Demande statistiques évaluations événement {}", evenementId);

        try {
            Map<String, Object> statistiques = feedbackService.obtenirStatistiquesEvaluations(evenementId);
            
            logger.debug("Statistiques calculées pour événement {}: {} évaluations", 
                        evenementId, statistiques.get("totalEvaluations"));
            
            return ResponseEntity.ok(statistiques);

        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Marque une évaluation comme utile
     * 
     * @param evaluationId ID de l'évaluation
     * @return confirmation
     */
    @PostMapping("/evaluations/{evaluationId}/utile")
    public ResponseEntity<Map<String, String>> marquerEvaluationUtile(@PathVariable Long evaluationId) {
        logger.info("Marquage évaluation {} comme utile", evaluationId);

        try {
            feedbackService.marquerEvaluationUtile(evaluationId);
            return ResponseEntity.ok(Map.of("message", "Évaluation marquée comme utile"));

        } catch (Exception e) {
            logger.error("Erreur lors du marquage utile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Signale une évaluation comme inappropriée
     * 
     * @param evaluationId ID de l'évaluation
     * @return confirmation
     */
    @PostMapping("/evaluations/{evaluationId}/signaler")
    public ResponseEntity<Map<String, String>> signalerEvaluation(@PathVariable Long evaluationId) {
        logger.info("Signalement évaluation {}", evaluationId);

        try {
            feedbackService.signalerEvaluation(evaluationId);
            return ResponseEntity.ok(Map.of("message", "Évaluation signalée"));

        } catch (Exception e) {
            logger.error("Erreur lors du signalement: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ================== ENQUÊTES DE SATISFACTION ==================

    /**
     * Crée une enquête de satisfaction
     * 
     * @param enqueteRequest requête de création d'enquête
     * @return l'enquête créée
     */
    @PostMapping("/enquetes")
    public ResponseEntity<EnqueteSatisfaction> creerEnqueteSatisfaction(
            @Valid @RequestBody EnqueteRequest enqueteRequest) {
        
        logger.info("Création enquête satisfaction: événement={}, titre={}", 
                   enqueteRequest.getEvenementId(), enqueteRequest.getTitre());

        try {
            EnqueteSatisfaction enquete = feedbackService.creerEnqueteSatisfaction(
                enqueteRequest.getEvenementId(),
                enqueteRequest.getTitre(),
                enqueteRequest.getDescription(),
                enqueteRequest.getQuestions(),
                enqueteRequest.getDureeJours(),
                enqueteRequest.getObligatoire()
            );

            logger.info("Enquête créée avec succès ID: {}", enquete.getId());
            return ResponseEntity.ok(enquete);

        } catch (Exception e) {
            logger.error("Erreur lors de la création d'enquête: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Soumet une réponse à une enquête
     * 
     * @param reponseRequest requête de réponse
     * @param request requête HTTP pour récupérer l'IP
     * @return la réponse créée
     */
    @PostMapping("/enquetes/reponses")
    public ResponseEntity<ReponseEnquete> soumettreReponseEnquete(
            @Valid @RequestBody ReponseEnqueteRequest reponseRequest,
            HttpServletRequest request) {
        
        logger.info("Soumission réponse enquête: enquête={}, utilisateur={}, anonyme={}", 
                   reponseRequest.getEnqueteId(), reponseRequest.getUtilisateurId(), 
                   reponseRequest.getAnonyme());

        try {
            String adresseIp = getClientIpAddress(request);
            
            ReponseEnquete reponse = feedbackService.soumettreReponseEnquete(
                reponseRequest.getEnqueteId(),
                reponseRequest.getUtilisateurId(),
                reponseRequest.getReponses(),
                reponseRequest.getAnonyme(),
                adresseIp
            );

            logger.info("Réponse enquête créée avec succès ID: {}", reponse.getId());
            return ResponseEntity.ok(reponse);

        } catch (Exception e) {
            logger.error("Erreur lors de la soumission de réponse: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtient les statistiques d'une enquête
     * 
     * @param enqueteId ID de l'enquête
     * @return statistiques de l'enquête
     */
    @GetMapping("/enquetes/statistiques/{enqueteId}")
    public ResponseEntity<Map<String, Object>> obtenirStatistiquesEnquete(@PathVariable Long enqueteId) {
        logger.info("Demande statistiques enquête {}", enqueteId);

        try {
            Map<String, Object> statistiques = feedbackService.obtenirStatistiquesEnquete(enqueteId);
            return ResponseEntity.ok(statistiques);

        } catch (Exception e) {
            logger.error("Erreur lors du calcul des statistiques d'enquête: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ================== MODÉRATION (ADMIN SEULEMENT) ==================

    /**
     * Obtient les évaluations nécessitant une modération
     * 
     * @param page numéro de page (défaut: 0)
     * @param size taille de page (défaut: 20)
     * @return page d'évaluations à modérer
     */
    @GetMapping("/evaluations/moderation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Evaluation>> obtenirEvaluationsAModerer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("Récupération évaluations à modérer: page={}, size={}", page, size);

        try {
            Page<Evaluation> evaluations = feedbackService.obtenirEvaluationsAModerer(page, size);
            return ResponseEntity.ok(evaluations);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des évaluations à modérer: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Modère une évaluation
     * 
     * @param evaluationId ID de l'évaluation
     * @param moderationRequest requête de modération
     * @return confirmation
     */
    @PostMapping("/evaluations/{evaluationId}/moderer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> modererEvaluation(
            @PathVariable Long evaluationId,
            @Valid @RequestBody ModerationRequest moderationRequest) {
        
        logger.info("Modération évaluation {}: approuvée={}", evaluationId, moderationRequest.getApprouvee());

        try {
            feedbackService.modererEvaluation(evaluationId, moderationRequest.getApprouvee());
            return ResponseEntity.ok(Map.of("message", "Évaluation modérée avec succès"));

        } catch (Exception e) {
            logger.error("Erreur lors de la modération: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtient le résumé des feedbacks pour un organisateur
     * 
     * @param organisateurId ID de l'organisateur
     * @return résumé des feedbacks
     */
    @GetMapping("/resume/organisateur/{organisateurId}")
    @PreAuthorize("hasRole('ADMIN') or @utilisateurService.isCurrentUserOrAdmin(#organisateurId)")
    public ResponseEntity<Map<String, Object>> obtenirResumeFeedbacksOrganisateur(@PathVariable Long organisateurId) {
        logger.info("Demande résumé feedbacks organisateur {}", organisateurId);

        try {
            Map<String, Object> resume = feedbackService.obtenirResumeFeedbacksOrganisateur(organisateurId);
            return ResponseEntity.ok(resume);

        } catch (Exception e) {
            logger.error("Erreur lors du calcul du résumé: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ================== CLASSES INTERNES POUR REQUÊTES ==================

    public static class EvaluationRequest {
        @NotNull private Long utilisateurId;
        @NotNull private Long evenementId;
        @NotNull @Min(1) @Max(5) private Integer note;
        @Size(max = 1000) private String commentaire;
        private Boolean anonyme = false;

        // Getters et setters
        public Long getUtilisateurId() { return utilisateurId; }
        public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
        public Long getEvenementId() { return evenementId; }
        public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }
        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }
        public String getCommentaire() { return commentaire; }
        public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
        public Boolean getAnonyme() { return anonyme; }
        public void setAnonyme(Boolean anonyme) { this.anonyme = anonyme; }
    }

    public static class EnqueteRequest {
        @NotNull private Long evenementId;
        @NotNull private Long organisateurId;
        @NotBlank @Size(max = 200) private String titre;
        @Size(max = 1000) private String description;
        private String questions;
        @Min(1) @Max(365) private Integer dureeJours = 30;
        private Boolean obligatoire = false;

        // Getters et setters
        public Long getEvenementId() { return evenementId; }
        public void setEvenementId(Long evenementId) { this.evenementId = evenementId; }
        public Long getOrganisateurId() { return organisateurId; }
        public void setOrganisateurId(Long organisateurId) { this.organisateurId = organisateurId; }
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getQuestions() { return questions; }
        public void setQuestions(String questions) { this.questions = questions; }
        public Integer getDureeJours() { return dureeJours; }
        public void setDureeJours(Integer dureeJours) { this.dureeJours = dureeJours; }
        public Boolean getObligatoire() { return obligatoire; }
        public void setObligatoire(Boolean obligatoire) { this.obligatoire = obligatoire; }
    }

    public static class ReponseEnqueteRequest {
        @NotNull private Long enqueteId;
        private Long utilisateurId;
        @NotBlank private String reponses;
        private Boolean anonyme = false;

        // Getters et setters
        public Long getEnqueteId() { return enqueteId; }
        public void setEnqueteId(Long enqueteId) { this.enqueteId = enqueteId; }
        public Long getUtilisateurId() { return utilisateurId; }
        public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }
        public String getReponses() { return reponses; }
        public void setReponses(String reponses) { this.reponses = reponses; }
        public Boolean getAnonyme() { return anonyme; }
        public void setAnonyme(Boolean anonyme) { this.anonyme = anonyme; }
    }

    public static class ModerationRequest {
        @NotNull private Boolean approuvee;

        public Boolean getApprouvee() { return approuvee; }
        public void setApprouvee(Boolean approuvee) { this.approuvee = approuvee; }
    }

    // ================== MÉTHODES UTILITAIRES ==================

    /**
     * Récupère l'adresse IP du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Endpoint de santé pour vérifier le bon fonctionnement du service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "FeedbackService",
            "features", new String[]{"evaluations_5_etoiles", "enquetes_satisfaction", "moderation"},
            "timestamp", java.time.LocalDateTime.now(),
            "version", "2.0.0"
        ));
    }
}
