package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un utilisateur de l'application EventConnect
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false, length = 50)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false, length = 50)
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(nullable = false)
    private String motDePasse;

    @Size(max = 15, message = "Le téléphone ne peut pas dépasser 15 caractères")
    @Column(length = 15)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUtilisateur role = RoleUtilisateur.USER;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "actif")
    private Boolean actif = true;

    // Relations
    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evenement> evenementsOrganises = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Méthode appelée avant la mise à jour de l'entité
     */
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Enum pour les rôles d'utilisateur
     */
    public enum RoleUtilisateur {
        USER("Utilisateur"),
        ADMIN("Administrateur"),
        ORGANISATEUR("Organisateur");

        private final String libelle;

        RoleUtilisateur(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    /**
     * Méthode utilitaire pour obtenir le nom complet
     */
    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    /**
     * Méthode utilitaire pour vérifier si l'utilisateur est administrateur
     */
    public boolean isAdmin() {
        return this.role == RoleUtilisateur.ADMIN;
    }

    /**
     * Méthode utilitaire pour vérifier si l'utilisateur est organisateur
     */
    public boolean isOrganisateur() {
        return this.role == RoleUtilisateur.ORGANISATEUR || this.role == RoleUtilisateur.ADMIN;
    }
}
