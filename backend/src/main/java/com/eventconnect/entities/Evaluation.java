package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entité représentant une évaluation d'événement par un utilisateur
 * Système de notation 5 étoiles avec commentaires
 *
 * @author EventConnect Team
 * @since 2.0.0
 */
@Entity
@Table(name = "evaluations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "evenement_id"}))
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    @NotNull(message = "L'événement est obligatoire")
    private Evenement evenement;

    @Column(name = "note", nullable = false)
    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimum est 1 étoile")
    @Max(value = 5, message = "La note maximum est 5 étoiles")
    private Integer note;

    @Column(name = "commentaire", length = 1000)
    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false)
    @NotNull
    private LocalDateTime dateEvaluation;

    @Column(name = "modere", nullable = false)
    private Boolean modere = false;

    @Column(name = "visible", nullable = false)
    private Boolean visible = true;

    @Column(name = "anonyme", nullable = false)
    private Boolean anonyme = false;

    @Column(name = "recommande", nullable = false)
    private Boolean recommande = false;

    @Column(name = "signale", nullable = false)
    private Integer signale = 0;

    @Column(name = "utile", nullable = false)
    private Integer utile = 0;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    /**
     * Constructeur par défaut
     */
    public Evaluation() {
        this.dateEvaluation = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres principaux
     *
     * @param utilisateur l'utilisateur qui évalue
     * @param evenement l'événement évalué
     * @param note la note de 1 à 5 étoiles
     * @param commentaire le commentaire optionnel
     */
    public Evaluation(Utilisateur utilisateur, Evenement evenement, Integer note, String commentaire) {
        this();
        this.utilisateur = utilisateur;
        this.evenement = evenement;
        this.note = note;
        this.commentaire = commentaire;
        this.recommande = note >= 4; // Recommandé si 4+ étoiles
    }

    /**
     * Méthode appelée avant la persistance pour valider les données
     */
    @PrePersist
    public void prePersist() {
        this.dateEvaluation = LocalDateTime.now();
        this.modere = false;
        this.visible = true;
        this.anonyme = false;
        this.recommande = this.note >= 4;
        this.signale = 0;
        this.utile = 0;
        this.actif = true;
    }

    /**
     * Modère l'évaluation (pour les administrateurs)
     */
    public void moderer() {
        this.modere = true;
    }

    /**
     * Masque l'évaluation
     */
    public void masquer() {
        this.visible = false;
    }

    /**
     * Affiche l'évaluation
     */
    public void afficher() {
        this.visible = true;
    }

    /**
     * Signale l'évaluation comme inappropriée
     */
    public void signaler() {
        this.signale++;
        if (this.signale >= 3) {
            this.visible = false; // Auto-masquage après 3 signalements
        }
    }

    /**
     * Marque l'évaluation comme utile
     */
    public void marquerUtile() {
        this.utile++;
    }

    /**
     * Vérifie si l'évaluation est positive (4+ étoiles)
     *
     * @return true si l'évaluation est positive
     */
    public boolean isPositive() {
        return this.note >= 4;
    }

    /**
     * Vérifie si l'évaluation nécessite une modération
     *
     * @return true si modération nécessaire
     */
    public boolean necessiteModerationl() {
        return this.signale >= 2 ||
                (this.commentaire != null && this.commentaire.length() > 500) ||
                this.note <= 2;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
        this.recommande = note >= 4;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Boolean getModere() {
        return modere;
    }

    public void setModere(Boolean modere) {
        this.modere = modere;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getAnonyme() {
        return anonyme;
    }

    public void setAnonyme(Boolean anonyme) {
        this.anonyme = anonyme;
    }

    public Boolean getRecommande() {
        return recommande;
    }

    public void setRecommande(Boolean recommande) {
        this.recommande = recommande;
    }

    public Integer getSignale() {
        return signale;
    }

    public void setSignale(Integer signale) {
        this.signale = signale;
    }

    public Integer getUtile() {
        return utile;
    }

    public void setUtile(Integer utile) {
        this.utile = utile;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return String.format("Evaluation{id=%d, utilisateur=%s, evenement=%s, note=%d, recommande=%s}",
                id,
                utilisateur != null ? utilisateur.getEmail() : "null",
                evenement != null ? evenement.getTitre() : "null",
                note,
                recommande);
    }
}