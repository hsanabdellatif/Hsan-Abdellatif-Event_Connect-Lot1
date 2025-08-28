package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un rôle utilisateur dans l'application EventConnect
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom du rôle doit contenir entre 2 et 50 caractères")
    @Column(nullable = false, unique = true, length = 50)
    private String nom;

    @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères")
    @Column(length = 200)
    private String description;

    @Column(name = "actif")
    private Boolean actif = true;

    // Constructeurs
    public Role() {}

    public Role(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Role(Long id, String nom, String description, Boolean actif) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.actif = actif;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    // Méthodes utilitaires
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return nom != null ? nom.equals(role.nom) : role.nom == null;
    }

    @Override
    public int hashCode() {
        return nom != null ? nom.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", actif=" + actif +
                '}';
    }

    /**
     * Enum pour les rôles prédéfinis
     */
    public enum RoleName {
        ADMIN("ADMIN", "Administrateur système"),
        ORGANISATEUR("ORGANISATEUR", "Organisateur d'événements"),
        PARTICIPANT("PARTICIPANT", "Participant aux événements");

        private final String nom;
        private final String description;

        RoleName(String nom, String description) {
            this.nom = nom;
            this.description = description;
        }

        public String getNom() { return nom; }
        public String getDescription() { return description; }
    }
}
