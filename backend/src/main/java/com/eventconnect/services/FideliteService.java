package com.eventconnect.services;

import com.eventconnect.entities.Badge;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.entities.UtilisateurBadge;
import com.eventconnect.repositories.BadgeRepository;
import com.eventconnect.repositories.UtilisateurBadgeRepository;
import com.eventconnect.repositories.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion du système de fidélité et des badges
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Service
@Transactional
public class FideliteService {

    private static final Logger log = LoggerFactory.getLogger(FideliteService.class);

    private final UtilisateurRepository utilisateurRepository;
    private final BadgeRepository badgeRepository;
    private final UtilisateurBadgeRepository utilisateurBadgeRepository;

    public FideliteService(UtilisateurRepository utilisateurRepository,
                          BadgeRepository badgeRepository,
                          UtilisateurBadgeRepository utilisateurBadgeRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.badgeRepository = badgeRepository;
        this.utilisateurBadgeRepository = utilisateurBadgeRepository;
    }

    /**
     * Attribue des points de fidélité à un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @param points le nombre de points à ajouter
     * @return la liste des nouveaux badges obtenus
     */
    public List<Badge> attribuerPoints(Long utilisateurId, int points) {
        log.info("Attribution de {} points à l'utilisateur ID: {}", points, utilisateurId);

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + utilisateurId));

        int ancienScore = utilisateur.getPointsFidelite();
        utilisateur.ajouterPoints(points);
        
        List<Badge> nouveauxBadges = verifierNouveauxBadges(utilisateur, ancienScore);
        
        utilisateurRepository.save(utilisateur);
        
        log.info("Utilisateur {} a maintenant {} points et {} nouveaux badges", 
                utilisateur.getEmail(), utilisateur.getPointsFidelite(), nouveauxBadges.size());
        
        return nouveauxBadges;
    }

    /**
     * Vérifie et attribue les nouveaux badges obtenus par un utilisateur
     * @param utilisateur l'utilisateur
     * @param ancienScore l'ancien score de points
     * @return la liste des nouveaux badges obtenus
     */
    private List<Badge> verifierNouveauxBadges(Utilisateur utilisateur, int ancienScore) {
        List<Badge> nouveauxBadges = new ArrayList<>();
        
        // Badges basés sur les points
        List<Badge> badgesAtteignables = badgeRepository.findBadgesAtteignables(utilisateur.getPointsFidelite());
        
        for (Badge badge : badgesAtteignables) {
            // Si le badge nécessite plus de points que l'ancien score et que l'utilisateur ne l'a pas déjà
            if (badge.getPointsRequis() > ancienScore && 
                !utilisateurBadgeRepository.existsByUtilisateurAndBadge(utilisateur, badge)) {
                
                attribuerBadge(utilisateur, badge, "Obtenu avec " + utilisateur.getPointsFidelite() + " points");
                nouveauxBadges.add(badge);
            }
        }

        // Badges basés sur l'activité
        verifierBadgesActivite(utilisateur, nouveauxBadges);
        
        return nouveauxBadges;
    }

    /**
     * Vérifie les badges basés sur l'activité de l'utilisateur
     * @param utilisateur l'utilisateur
     * @param nouveauxBadges liste à laquelle ajouter les nouveaux badges
     */
    private void verifierBadgesActivite(Utilisateur utilisateur, List<Badge> nouveauxBadges) {
        // Badge Participant Actif (10 réservations)
        if (utilisateur.getTotalReservations() >= 10) {
            Badge badgeParticipant = badgeRepository.findByNomAndActifTrue("Participant Actif").orElse(null);
            if (badgeParticipant != null && 
                !utilisateurBadgeRepository.existsByUtilisateurAndBadge(utilisateur, badgeParticipant)) {
                attribuerBadge(utilisateur, badgeParticipant, 
                    "Obtenu avec " + utilisateur.getTotalReservations() + " réservations");
                nouveauxBadges.add(badgeParticipant);
            }
        }

        // Badge Organisateur (5 événements organisés)
        if (utilisateur.getTotalEvenementsOrganises() >= 5) {
            Badge badgeOrganisateur = badgeRepository.findByNomAndActifTrue("Organisateur Expert").orElse(null);
            if (badgeOrganisateur != null && 
                !utilisateurBadgeRepository.existsByUtilisateurAndBadge(utilisateur, badgeOrganisateur)) {
                attribuerBadge(utilisateur, badgeOrganisateur, 
                    "Obtenu avec " + utilisateur.getTotalEvenementsOrganises() + " événements organisés");
                nouveauxBadges.add(badgeOrganisateur);
            }
        }
    }

    /**
     * Attribue un badge à un utilisateur
     * @param utilisateur l'utilisateur
     * @param badge le badge à attribuer
     * @param commentaire commentaire sur l'obtention
     */
    private void attribuerBadge(Utilisateur utilisateur, Badge badge, String commentaire) {
        UtilisateurBadge utilisateurBadge = new UtilisateurBadge(
            utilisateur, badge, utilisateur.getPointsFidelite());
        utilisateurBadge.setCommentaire(commentaire);
        
        utilisateurBadgeRepository.save(utilisateurBadge);
        
        log.info("Badge '{}' attribué à l'utilisateur {}", badge.getNom(), utilisateur.getEmail());
    }

    /**
     * Récompense un utilisateur pour une réservation
     * @param utilisateurId l'ID de l'utilisateur
     */
    public void recompenserReservation(Long utilisateurId) {
        log.info("Récompense pour réservation - Utilisateur ID: {}", utilisateurId);
        
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        utilisateur.incrementerReservations();
        attribuerPoints(utilisateurId, 10); // 10 points par réservation
    }

    /**
     * Récompense un utilisateur pour l'organisation d'un événement
     * @param utilisateurId l'ID de l'utilisateur organisateur
     */
    public void recompenserOrganisation(Long utilisateurId) {
        log.info("Récompense pour organisation d'événement - Utilisateur ID: {}", utilisateurId);
        
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        utilisateur.incrementerEvenementsOrganises();
        attribuerPoints(utilisateurId, 50); // 50 points par événement organisé
    }

    /**
     * Obtient tous les badges d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return la liste des badges de l'utilisateur
     */
    @Transactional(readOnly = true)
    public List<UtilisateurBadge> obtenirBadgesUtilisateur(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return utilisateurBadgeRepository.findByUtilisateurOrderByDateObtentionDesc(utilisateur);
    }

    /**
     * Obtient le profil de fidélité d'un utilisateur
     * @param utilisateurId l'ID de l'utilisateur
     * @return l'utilisateur avec ses informations de fidélité
     */
    @Transactional(readOnly = true)
    public Utilisateur obtenirProfilFidelite(Long utilisateurId) {
        return utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    /**
     * Obtient tous les badges disponibles
     * @return la liste de tous les badges actifs
     */
    @Transactional(readOnly = true)
    public List<Badge> obtenirTousLesBadges() {
        return badgeRepository.findByActifTrue();
    }

    /**
     * Obtient les prochains badges qu'un utilisateur peut obtenir
     * @param utilisateurId l'ID de l'utilisateur
     * @return la liste des prochains badges atteignables
     */
    @Transactional(readOnly = true)
    public List<Badge> obtenirProchainsBadges(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return badgeRepository.findProchainsBadges(utilisateur.getPointsFidelite());
    }
}
