package com.eventconnect.dto;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un créneau libre dans l'agenda
 * Utilisé pour proposer des alternatives de planification
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
public class CreneauLibreDto {

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int dureeMinutes;
    private String jourSemaine;
    private String trancheHoraire; // MATIN, APRES_MIDI, SOIR
    private int scoreProximite; // Score de proximité avec la demande originale (0-100)
    private String description;
    private boolean recommande; // Indique si c'est un créneau recommandé

    /**
     * Constructeur par défaut
     */
    public CreneauLibreDto() {}

    /**
     * Constructeur avec paramètres principaux
     * 
     * @param dateDebut date de début du créneau libre
     * @param dateFin date de fin du créneau libre
     */
    public CreneauLibreDto(LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
        this.jourSemaine = dateDebut.getDayOfWeek().name();
        this.trancheHoraire = determinerTrancheHoraire(dateDebut);
    }

    /**
     * Constructeur complet
     * 
     * @param dateDebut date de début du créneau libre
     * @param dateFin date de fin du créneau libre
     * @param scoreProximite score de proximité avec la demande originale
     * @param description description du créneau
     */
    public CreneauLibreDto(LocalDateTime dateDebut, LocalDateTime dateFin, 
                          int scoreProximite, String description) {
        this(dateDebut, dateFin);
        this.scoreProximite = scoreProximite;
        this.description = description;
        this.recommande = scoreProximite >= 80; // Recommandé si score élevé
    }

    /**
     * Détermine la tranche horaire basée sur l'heure de début
     * 
     * @param dateTime date et heure
     * @return tranche horaire
     */
    private String determinerTrancheHoraire(LocalDateTime dateTime) {
        int heure = dateTime.getHour();
        if (heure >= 8 && heure < 12) {
            return "MATIN";
        } else if (heure >= 12 && heure < 17) {
            return "APRES_MIDI";
        } else if (heure >= 17 && heure <= 22) {
            return "SOIR";
        } else {
            return "HORS_HEURES";
        }
    }

    // Getters et Setters

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
        if (dateDebut != null) {
            this.jourSemaine = dateDebut.getDayOfWeek().name();
            this.trancheHoraire = determinerTrancheHoraire(dateDebut);
            if (dateFin != null) {
                this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
            }
        }
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
        if (dateFin != null && dateDebut != null) {
            this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
        }
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public String getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(String jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public String getTrancheHoraire() {
        return trancheHoraire;
    }

    public void setTrancheHoraire(String trancheHoraire) {
        this.trancheHoraire = trancheHoraire;
    }

    public int getScoreProximite() {
        return scoreProximite;
    }

    public void setScoreProximite(int scoreProximite) {
        this.scoreProximite = scoreProximite;
        this.recommande = scoreProximite >= 80;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRecommande() {
        return recommande;
    }

    public void setRecommande(boolean recommande) {
        this.recommande = recommande;
    }

    /**
     * Calcule un score de qualité du créneau basé sur plusieurs critères
     * 
     * @return score de qualité (0-100)
     */
    public int getScoreQualite() {
        int score = 0;
        
        // Bonus pour les créneaux en semaine
        if (dateDebut != null) {
            switch (dateDebut.getDayOfWeek()) {
                case MONDAY:
                case TUESDAY:
                case WEDNESDAY:
                case THURSDAY:
                    score += 20;
                    break;
                case FRIDAY:
                    score += 15;
                    break;
                case SATURDAY:
                case SUNDAY:
                    score += 10;
                    break;
            }
        }
        
        // Bonus pour les bonnes tranches horaires
        switch (trancheHoraire) {
            case "MATIN":
                score += 30;
                break;
            case "APRES_MIDI":
                score += 35;
                break;
            case "SOIR":
                score += 25;
                break;
            default:
                score += 5;
                break;
        }
        
        // Bonus pour durée appropriée
        if (dureeMinutes >= 60 && dureeMinutes <= 240) {
            score += 25;
        } else if (dureeMinutes >= 30 && dureeMinutes < 60) {
            score += 15;
        } else {
            score += 5;
        }
        
        // Bonus de proximité
        score += Math.min(scoreProximite / 4, 25);
        
        return Math.min(score, 100);
    }

    @Override
    public String toString() {
        return String.format("CreneauLibre{%s - %s, durée=%dmin, score=%d, recommandé=%s}", 
                           dateDebut, dateFin, dureeMinutes, scoreProximite, recommande);
    }
}
