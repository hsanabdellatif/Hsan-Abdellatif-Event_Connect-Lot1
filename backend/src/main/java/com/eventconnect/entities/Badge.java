package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entité représentant un badge dans le système de fidélité EventConnect
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du badge est obligatoire")
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @NotBlank(message = "La description du badge est obligatoire")
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull(message = "Les points requis sont obligatoires")
    @Column(name = "points_requis", nullable = false)
    private Integer pointsRequis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeBadge type;

    @Column(name = "icone", length = 100)
    private String icone;

    @Column(name = "couleur", length = 20)
    private String couleur;

    @Column(name = "actif")
    private Boolean actif = true;

    // Constructeurs
    public Badge() {}

    public Badge(String nom, String description, Integer pointsRequis, TypeBadge type) {
        this.nom = nom;
        this.description = description;
        this.pointsRequis = pointsRequis;
        this.type = type;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPointsRequis() { return pointsRequis; }
    public void setPointsRequis(Integer pointsRequis) { this.pointsRequis = pointsRequis; }

    public TypeBadge getType() { return type; }
    public void setType(TypeBadge type) { this.type = type; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    /**
     * Enum pour les types de badges
     */
    public enum TypeBadge {
        DEBUTANT("Débutant"),
        PARTICIPANT_ACTIF("Participant Actif"),
        ORGANISATEUR("Organisateur"),
        VIP("VIP"),
        FIDELITE("Fidélité"),
        SPECIAL("Spécial");

        private final String libelle;

        TypeBadge(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    @Override
    public String toString() {
        return "Badge{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", pointsRequis=" + pointsRequis +
                ", type=" + type +
                '}';
    }
}
