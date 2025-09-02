package com.eventconnect.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO pour les statistiques d'évaluation d'un événement
 * Contient toutes les métriques de satisfaction et feedback
 *
 * @author EventConnect Team
 * @since 2.0.0
 */
public class StatistiquesEvaluationDto {

    private Long totalEvaluations;
    private Double noteMoyenne;
    private Long cinqEtoiles;
    private Long quatreEtoiles;
    private Long troisEtoiles;
    private Long deuxEtoiles;
    private Long uneEtoile;
    private Long recommandations;
    private Double pourcentageRecommandation;
    private LocalDateTime derniereEvaluation;

    /**
     * Constructeur par défaut
     */
    public StatistiquesEvaluationDto() {}

    /**
     * Constructeur avec paramètres principaux
     *
     * @param totalEvaluations nombre total d'évaluations
     * @param noteMoyenne note moyenne
     * @param recommandations nombre de recommandations
     */
    public StatistiquesEvaluationDto(Long totalEvaluations, Double noteMoyenne, Long recommandations) {
        this.totalEvaluations = totalEvaluations;
        this.noteMoyenne = noteMoyenne;
        this.recommandations = recommandations;
        this.pourcentageRecommandation = totalEvaluations > 0 ?
                (recommandations * 100.0 / totalEvaluations) : 0.0;
    }

    // Getters et Setters

    public Long getTotalEvaluations() {
        return totalEvaluations;
    }

    public void setTotalEvaluations(Long totalEvaluations) {
        this.totalEvaluations = totalEvaluations;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public Long getCinqEtoiles() {
        return cinqEtoiles;
    }

    public void setCinqEtoiles(Long cinqEtoiles) {
        this.cinqEtoiles = cinqEtoiles;
    }

    public Long getQuatreEtoiles() {
        return quatreEtoiles;
    }

    public void setQuatreEtoiles(Long quatreEtoiles) {
        this.quatreEtoiles = quatreEtoiles;
    }

    public Long getTroisEtoiles() {
        return troisEtoiles;
    }

    public void setTroisEtoiles(Long troisEtoiles) {
        this.troisEtoiles = troisEtoiles;
    }

    public Long getDeuxEtoiles() {
        return deuxEtoiles;
    }

    public void setDeuxEtoiles(Long deuxEtoiles) {
        this.deuxEtoiles = deuxEtoiles;
    }

    public Long getUneEtoile() {
        return uneEtoile;
    }

    public void setUneEtoile(Long uneEtoile) {
        this.uneEtoile = uneEtoile;
    }

    public Long getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(Long recommandations) {
        this.recommandations = recommandations;
        // Recalculer le pourcentage
        if (this.totalEvaluations != null && this.totalEvaluations > 0) {
            this.pourcentageRecommandation = recommandations * 100.0 / this.totalEvaluations;
        }
    }

    public Double getPourcentageRecommandation() {
        return pourcentageRecommandation;
    }

    public void setPourcentageRecommandation(Double pourcentageRecommandation) {
        this.pourcentageRecommandation = pourcentageRecommandation;
    }

    public LocalDateTime getDerniereEvaluation() {
        return derniereEvaluation;
    }

    public void setDerniereEvaluation(LocalDateTime derniereEvaluation) {
        this.derniereEvaluation = derniereEvaluation;
    }

    /**
     * Calcule le score de satisfaction (0-100)
     * Basé sur la répartition des notes
     *
     * @return score de satisfaction
     */
    public Double getScoreSatisfaction() {
        if (totalEvaluations == null || totalEvaluations == 0) {
            return 0.0;
        }

        long total = totalEvaluations;
        long positives = (cinqEtoiles != null ? cinqEtoiles : 0) +
                (quatreEtoiles != null ? quatreEtoiles : 0);
        long neutres = troisEtoiles != null ? troisEtoiles : 0;
        long negatives = (deuxEtoiles != null ? deuxEtoiles : 0) +
                (uneEtoile != null ? uneEtoile : 0);

        // Score pondéré : positives=100%, neutres=50%, négatives=0%
        double score = ((positives * 100.0) + (neutres * 50.0)) / total;
        return Math.round(score * 100.0) / 100.0; // Arrondir à 2 décimales
    }

    /**
     * Détermine le niveau de satisfaction basé sur le score
     *
     * @return niveau de satisfaction
     */
    public String getNiveauSatisfaction() {
        Double score = getScoreSatisfaction();
        if (score >= 80) return "EXCELLENT";
        else if (score >= 60) return "BON";
        else if (score >= 40) return "MOYEN";
        else if (score >= 20) return "FAIBLE";
        else return "TRÈS_FAIBLE";
    }

    /**
     * Vérifie si les statistiques indiquent une bonne satisfaction
     *
     * @return true si satisfaction bonne (score >= 60%)
     */
    public boolean isGoodSatisfaction() {
        return getScoreSatisfaction() >= 60.0;
    }

    @Override
    public String toString() {
        return String.format("StatistiquesEvaluation{total=%d, moyenne=%.2f, recommandations=%d (%.1f%%), satisfaction=%s}",
                totalEvaluations, noteMoyenne, recommandations,
                pourcentageRecommandation, getNiveauSatisfaction());
    }
}