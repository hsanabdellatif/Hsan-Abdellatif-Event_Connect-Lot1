package com.eventconnect.services;

import com.eventconnect.entities.*;
import com.eventconnect.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service pour la gestion des évaluations et feedbacks d'événements
 * Gère le système de notation 5 étoiles et les enquêtes de satisfaction
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@Service
@Transactional
public class FeedbackService {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private EnqueteSatisfactionRepository enqueteRepository;

    @Autowired
    private ReponseEnqueteRepository reponseEnqueteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // ================== GESTION DES ÉVALUATIONS ==================

    /**
     * Crée ou met à jour une évaluation d'événement
     * 
     * @param utilisateurId ID de l'utilisateur
     * @param evenementId ID de l'événement
     * @param note note de 1 à 5 étoiles
     * @param commentaire commentaire optionnel
     * @param anonyme si l'évaluation est anonyme
     * @return l'évaluation créée ou mise à jour
     */
    public Evaluation evaluerEvenement(Long utilisateurId, Long evenementId, Integer note, 
                                      String commentaire, Boolean anonyme) {
        logger.info("Création/mise à jour évaluation: utilisateur={}, événement={}, note={}", 
                   utilisateurId, evenementId, note);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + utilisateurId));

        Evenement evenement = evenementRepository.findById(evenementId)
            .orElseThrow(() -> new RuntimeException("Événement introuvable: " + evenementId));

        // Vérifier que l'utilisateur a bien participé à l'événement
        if (!aParticipleAEvenement(utilisateur, evenement)) {
            throw new RuntimeException("Vous ne pouvez évaluer que les événements auxquels vous avez participé");
        }

        // Vérifier que l'événement est terminé
        if (evenement.getDateFin().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Vous ne pouvez évaluer un événement qu'après sa fin");
        }

        Optional<Evaluation> evaluationExistante = evaluationRepository
            .findByUtilisateurAndEvenement(utilisateur, evenement);

        Evaluation evaluation;
        if (evaluationExistante.isPresent()) {
            // Mise à jour de l'évaluation existante
            evaluation = evaluationExistante.get();
            evaluation.setNote(note);
            evaluation.setCommentaire(commentaire);
            evaluation.setAnonyme(anonyme != null ? anonyme : false);
            evaluation.setDateEvaluation(LocalDateTime.now());
            logger.info("Mise à jour évaluation existante ID: {}", evaluation.getId());
        } else {
            // Création d'une nouvelle évaluation
            evaluation = new Evaluation(utilisateur, evenement, note, commentaire);
            evaluation.setAnonyme(anonyme != null ? anonyme : false);
            logger.info("Création nouvelle évaluation");
        }

        evaluation = evaluationRepository.save(evaluation);
        logger.info("Évaluation sauvegardée avec succès ID: {}", evaluation.getId());

        return evaluation;
    }

    /**
     * Récupère les évaluations d'un événement avec pagination
     * 
     * @param evenementId ID de l'événement
     * @param page numéro de page
     * @param size taille de page
     * @return page d'évaluations
     */
    @Transactional(readOnly = true)
    public Page<Evaluation> obtenirEvaluationsEvenement(Long evenementId, int page, int size) {
        Evenement evenement = evenementRepository.findById(evenementId)
            .orElseThrow(() -> new RuntimeException("Événement introuvable: " + evenementId));

        Pageable pageable = PageRequest.of(page, size);
        return evaluationRepository.findByEvenementVisible(evenement, pageable);
    }

    /**
     * Calcule les statistiques d'évaluation pour un événement
     * 
     * @param evenementId ID de l'événement
     * @return map avec les statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirStatistiquesEvaluations(Long evenementId) {
        Evenement evenement = evenementRepository.findById(evenementId)
            .orElseThrow(() -> new RuntimeException("Événement introuvable: " + evenementId));

        Object[] stats = evaluationRepository.getStatistiquesEvaluations(evenement);
        
        Map<String, Object> statistiques = new HashMap<>();
        if (stats != null && stats.length > 0) {
            statistiques.put("totalEvaluations", stats[0] != null ? ((Number) stats[0]).longValue() : 0L);
            statistiques.put("noteMoyenne", stats[1] != null ? ((Number) stats[1]).doubleValue() : 0.0);
            statistiques.put("cinqEtoiles", stats[2] != null ? ((Number) stats[2]).longValue() : 0L);
            statistiques.put("quatreEtoiles", stats[3] != null ? ((Number) stats[3]).longValue() : 0L);
            statistiques.put("troisEtoiles", stats[4] != null ? ((Number) stats[4]).longValue() : 0L);
            statistiques.put("deuxEtoiles", stats[5] != null ? ((Number) stats[5]).longValue() : 0L);
            statistiques.put("uneEtoile", stats[6] != null ? ((Number) stats[6]).longValue() : 0L);
            statistiques.put("recommandations", stats[7] != null ? ((Number) stats[7]).longValue() : 0L);
        } else {
            statistiques.put("totalEvaluations", 0L);
            statistiques.put("noteMoyenne", 0.0);
            statistiques.put("cinqEtoiles", 0L);
            statistiques.put("quatreEtoiles", 0L);
            statistiques.put("troisEtoiles", 0L);
            statistiques.put("deuxEtoiles", 0L);
            statistiques.put("uneEtoile", 0L);
            statistiques.put("recommandations", 0L);
        }

        // Calcul du pourcentage de recommandation
        long total = (Long) statistiques.get("totalEvaluations");
        long recommandations = (Long) statistiques.get("recommandations");
        double pourcentageRecommandation = total > 0 ? (recommandations * 100.0 / total) : 0.0;
        statistiques.put("pourcentageRecommandation", pourcentageRecommandation);

        logger.debug("Statistiques calculées pour événement {}: {} évaluations, moyenne {}", 
                    evenementId, total, statistiques.get("noteMoyenne"));

        return statistiques;
    }

    /**
     * Marque une évaluation comme utile
     * 
     * @param evaluationId ID de l'évaluation
     */
    public void marquerEvaluationUtile(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new RuntimeException("Évaluation introuvable: " + evaluationId));

        evaluation.marquerUtile();
        evaluationRepository.save(evaluation);
        
        logger.info("Évaluation {} marquée comme utile", evaluationId);
    }

    /**
     * Signale une évaluation comme inappropriée
     * 
     * @param evaluationId ID de l'évaluation
     */
    public void signalerEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new RuntimeException("Évaluation introuvable: " + evaluationId));

        evaluation.signaler();
        evaluationRepository.save(evaluation);
        
        logger.warn("Évaluation {} signalée. Nombre de signalements: {}", 
                   evaluationId, evaluation.getSignale());
    }

    // ================== GESTION DES ENQUÊTES ==================

    /**
     * Crée une enquête de satisfaction pour un événement
     * 
     * @param evenementId ID de l'événement
     * @param titre titre de l'enquête
     * @param description description
     * @param questions questions en format JSON
     * @param dureeJours durée de validité en jours
     * @param obligatoire si l'enquête est obligatoire
     * @return l'enquête créée
     */
    public EnqueteSatisfaction creerEnqueteSatisfaction(Long evenementId, String titre, 
                                                       String description, String questions, 
                                                       Integer dureeJours, Boolean obligatoire) {
        logger.info("Création enquête satisfaction pour événement {}: {}", evenementId, titre);

        Evenement evenement = evenementRepository.findById(evenementId)
            .orElseThrow(() -> new RuntimeException("Événement introuvable: " + evenementId));

        EnqueteSatisfaction enquete = new EnqueteSatisfaction(evenement, titre, description);
        enquete.setQuestions(questions);
        enquete.setObligatoire(obligatoire != null ? obligatoire : false);
        
        if (dureeJours != null && dureeJours > 0) {
            enquete.setDateExpiration(LocalDateTime.now().plusDays(dureeJours));
        }

        enquete = enqueteRepository.save(enquete);
        logger.info("Enquête créée avec succès ID: {}", enquete.getId());

        return enquete;
    }

    /**
     * Soumet une réponse à une enquête
     * 
     * @param enqueteId ID de l'enquête
     * @param utilisateurId ID de l'utilisateur (nullable si anonyme)
     * @param reponses réponses en format JSON
     * @param anonyme si la réponse est anonyme
     * @param adresseIp adresse IP du répondant
     * @return la réponse créée
     */
    public ReponseEnquete soumettreReponseEnquete(Long enqueteId, Long utilisateurId, 
                                                 String reponses, Boolean anonyme, String adresseIp) {
        logger.info("Soumission réponse enquête {}: utilisateur={}, anonyme={}", 
                   enqueteId, utilisateurId, anonyme);

        EnqueteSatisfaction enquete = enqueteRepository.findById(enqueteId)
            .orElseThrow(() -> new RuntimeException("Enquête introuvable: " + enqueteId));

        if (!enquete.isActiveEtValide()) {
            throw new RuntimeException("Cette enquête n'est plus active ou a expiré");
        }

        Utilisateur utilisateur = null;
        if (utilisateurId != null && !Boolean.TRUE.equals(anonyme)) {
            utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + utilisateurId));

            // Vérifier si l'utilisateur a déjà répondu
            if (reponseEnqueteRepository.existsByUtilisateurAndEnquete(utilisateur, enquete)) {
                throw new RuntimeException("Vous avez déjà répondu à cette enquête");
            }
        }

        ReponseEnquete reponse = new ReponseEnquete(utilisateur, enquete, reponses, 
                                                   anonyme != null ? anonyme : false);
        reponse.setAdresseIp(adresseIp);
        reponse.marquerComplete();

        reponse = reponseEnqueteRepository.save(reponse);
        logger.info("Réponse enquête sauvegardée avec succès ID: {}", reponse.getId());

        return reponse;
    }

    /**
     * Obtient les statistiques d'une enquête
     * 
     * @param enqueteId ID de l'enquête
     * @return map avec les statistiques
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirStatistiquesEnquete(Long enqueteId) {
        EnqueteSatisfaction enquete = enqueteRepository.findById(enqueteId)
            .orElseThrow(() -> new RuntimeException("Enquête introuvable: " + enqueteId));

        Map<String, Object> statistiques = new HashMap<>();
        
        long totalReponses = reponseEnqueteRepository.countByEnquete(enquete);
        long reponsesCompletes = reponseEnqueteRepository.countReponsesCompletesByEnquete(enquete);
        double tauxCompletion = reponseEnqueteRepository.calculateTauxCompletion(enquete);

        statistiques.put("totalReponses", totalReponses);
        statistiques.put("reponsesCompletes", reponsesCompletes);
        statistiques.put("tauxCompletion", tauxCompletion);
        statistiques.put("enqueteActive", enquete.isActiveEtValide());
        statistiques.put("dateExpiration", enquete.getDateExpiration());

        return statistiques;
    }

    // ================== MÉTHODES UTILITAIRES ==================

    /**
     * Vérifie si un utilisateur a participé à un événement
     * 
     * @param utilisateur l'utilisateur
     * @param evenement l'événement
     * @return true si l'utilisateur a participé
     */
    @Transactional(readOnly = true)
    public boolean aParticipleAEvenement(Utilisateur utilisateur, Evenement evenement) {
        return reservationRepository.findByUtilisateurAndEvenement(utilisateur, evenement)
            .stream()
            .anyMatch(reservation -> reservation.getStatut() == Reservation.StatutReservation.CONFIRMEE);
    }

    /**
     * Obtient les évaluations nécessitant une modération
     * 
     * @param page numéro de page
     * @param size taille de page
     * @return page d'évaluations à modérer
     */
    @Transactional(readOnly = true)
    public Page<Evaluation> obtenirEvaluationsAModerer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return evaluationRepository.findEvaluationsAModerer(pageable);
    }

    /**
     * Modère une évaluation (pour administrateurs)
     * 
     * @param evaluationId ID de l'évaluation
     * @param approuvee si l'évaluation est approuvée
     */
    public void modererEvaluation(Long evaluationId, Boolean approuvee) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new RuntimeException("Évaluation introuvable: " + evaluationId));

        evaluation.moderer();
        if (Boolean.FALSE.equals(approuvee)) {
            evaluation.masquer();
        } else {
            evaluation.afficher();
        }

        evaluationRepository.save(evaluation);
        logger.info("Évaluation {} modérée: {}", evaluationId, approuvee ? "approuvée" : "rejetée");
    }

    /**
     * Obtient le résumé des feedbacks pour un organisateur
     * 
     * @param organisateurId ID de l'organisateur
     * @return map avec le résumé
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenirResumeFeedbacksOrganisateur(Long organisateurId) {
        Utilisateur organisateur = utilisateurRepository.findById(organisateurId)
            .orElseThrow(() -> new RuntimeException("Organisateur introuvable: " + organisateurId));

        List<Evenement> evenements = evenementRepository.findByOrganisateur(organisateur);
        
        Map<String, Object> resume = new HashMap<>();
        long totalEvaluations = 0;
        double sommeMoyennes = 0.0;
        int evenementsAvecEvaluations = 0;

        for (Evenement evenement : evenements) {
            Long nbEvaluations = evaluationRepository.countByEvenementVisible(evenement);
            if (nbEvaluations > 0) {
                totalEvaluations += nbEvaluations;
                Double moyenne = evaluationRepository.calculateAverageNoteByEvenement(evenement);
                if (moyenne != null) {
                    sommeMoyennes += moyenne;
                    evenementsAvecEvaluations++;
                }
            }
        }

        resume.put("totalEvenements", evenements.size());
        resume.put("totalEvaluations", totalEvaluations);
        resume.put("noteMoyenneGlobale", evenementsAvecEvaluations > 0 ? sommeMoyennes / evenementsAvecEvaluations : 0.0);
        resume.put("evenementsAvecEvaluations", evenementsAvecEvaluations);

        return resume;
    }
}
