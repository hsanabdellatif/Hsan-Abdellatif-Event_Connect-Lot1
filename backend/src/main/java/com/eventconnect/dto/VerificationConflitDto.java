package com.eventconnect.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les résultats de vérification de conflit d'agenda
 * Contient les informations sur la présence de conflits et les détails associés
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
public class VerificationConflitDto {

    private boolean conflitDetecte;
    private String message;
    private List<ConflitDetailDto> conflits;
    private LocalDateTime dateVerification;
    private int nombreConflits;
    private String typeVerification; // ORGANISATEUR ou UTILISATEUR

    /**
     * Constructeur par défaut
     */
    public VerificationConflitDto() {
        this.dateVerification = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres principaux
     * 
     * @param conflitDetecte true si des conflits sont détectés
     * @param message message descriptif du résultat
     * @param conflits liste des conflits détectés
     */
    public VerificationConflitDto(boolean conflitDetecte, String message, List<ConflitDetailDto> conflits) {
        this();
        this.conflitDetecte = conflitDetecte;
        this.message = message;
        this.conflits = conflits;
        this.nombreConflits = conflits != null ? conflits.size() : 0;
    }

    // Getters et Setters

    public boolean isConflitDetecte() {
        return conflitDetecte;
    }

    public void setConflitDetecte(boolean conflitDetecte) {
        this.conflitDetecte = conflitDetecte;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ConflitDetailDto> getConflits() {
        return conflits;
    }

    public void setConflits(List<ConflitDetailDto> conflits) {
        this.conflits = conflits;
        this.nombreConflits = conflits != null ? conflits.size() : 0;
    }

    public LocalDateTime getDateVerification() {
        return dateVerification;
    }

    public void setDateVerification(LocalDateTime dateVerification) {
        this.dateVerification = dateVerification;
    }

    public int getNombreConflits() {
        return nombreConflits;
    }

    public void setNombreConflits(int nombreConflits) {
        this.nombreConflits = nombreConflits;
    }

    public String getTypeVerification() {
        return typeVerification;
    }

    public void setTypeVerification(String typeVerification) {
        this.typeVerification = typeVerification;
    }

    /**
     * Classe interne pour les détails d'un conflit
     */
    public static class ConflitDetailDto {
        private Long evenementId;
        private String nomEvenement;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String lieu;
        private String typeConflit; // TOTAL, PARTIEL_DEBUT, PARTIEL_FIN, ENGLOBE
        private String description;

        /**
         * Constructeur par défaut
         */
        public ConflitDetailDto() {}

        /**
         * Constructeur avec paramètres
         * 
         * @param evenementId ID de l'événement en conflit
         * @param nomEvenement nom de l'événement
         * @param dateDebut date de début de l'événement
         * @param dateFin date de fin de l'événement
         * @param lieu lieu de l'événement
         * @param typeConflit type de conflit détecté
         */
        public ConflitDetailDto(Long evenementId, String nomEvenement, LocalDateTime dateDebut, 
                               LocalDateTime dateFin, String lieu, String typeConflit) {
            this.evenementId = evenementId;
            this.nomEvenement = nomEvenement;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.lieu = lieu;
            this.typeConflit = typeConflit;
        }

        // Getters et Setters

        public Long getEvenementId() {
            return evenementId;
        }

        public void setEvenementId(Long evenementId) {
            this.evenementId = evenementId;
        }

        public String getNomEvenement() {
            return nomEvenement;
        }

        public void setNomEvenement(String nomEvenement) {
            this.nomEvenement = nomEvenement;
        }

        public LocalDateTime getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(LocalDateTime dateDebut) {
            this.dateDebut = dateDebut;
        }

        public LocalDateTime getDateFin() {
            return dateFin;
        }

        public void setDateFin(LocalDateTime dateFin) {
            this.dateFin = dateFin;
        }

        public String getLieu() {
            return lieu;
        }

        public void setLieu(String lieu) {
            this.lieu = lieu;
        }

        public String getTypeConflit() {
            return typeConflit;
        }

        public void setTypeConflit(String typeConflit) {
            this.typeConflit = typeConflit;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
