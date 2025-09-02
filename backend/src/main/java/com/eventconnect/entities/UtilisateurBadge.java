package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entité représentant l'attribution d'un badge à un utilisateur
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@Entity
@Table(name = "utilisateur_badges",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "badge_id"}))
public class UtilisateurBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    @NotNull(message = "Le badge est obligatoire")
    private Badge badge;

    @Column(name = "date_obtention", nullable = false)
    private LocalDateTime dateObtention = LocalDateTime.now();

    @Column(name = "points_au_moment_obtention")
    private Integer pointsAuMomentObtention;

    @Column(length = 500)
    private String commentaire;

    // Constructeurs
    public UtilisateurBadge() {}

    public UtilisateurBadge(Utilisateur utilisateur, Badge badge, Integer pointsAuMomentObtention) {
        this.utilisateur = utilisateur;
        this.badge = badge;
        this.pointsAuMomentObtention = pointsAuMomentObtention;
        this.dateObtention = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Badge getBadge() { return badge; }
    public void setBadge(Badge badge) { this.badge = badge; }

    public LocalDateTime getDateObtention() { return dateObtention; }
    public void setDateObtention(LocalDateTime dateObtention) { this.dateObtention = dateObtention; }

    public Integer getPointsAuMomentObtention() { return pointsAuMomentObtention; }
    public void setPointsAuMomentObtention(Integer pointsAuMomentObtention) {
        this.pointsAuMomentObtention = pointsAuMomentObtention;
    }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    @Override
    public String toString() {
        return "UtilisateurBadge{" +
                "id=" + id +
                ", utilisateur=" + (utilisateur != null ? utilisateur.getEmail() : "null") +
                ", badge=" + (badge != null ? badge.getNom() : "null") +
                ", dateObtention=" + dateObtention +
                '}';
    }
}