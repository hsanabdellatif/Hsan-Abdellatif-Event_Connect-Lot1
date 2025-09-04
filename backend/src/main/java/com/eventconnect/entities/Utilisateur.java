package com.eventconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
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

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "points_fidelite")
    private Integer pointsFidelite = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_fidelite")
    private NiveauFidelite niveauFidelite = NiveauFidelite.BRONZE;

    @Column(name = "total_reservations")
    private Integer totalReservations = 0;

    @Column(name = "total_evenements_organises")
    private Integer totalEvenementsOrganises = 0;

    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Changement ici pour éviter la récursion infinie
    private List<Evenement> evenementsOrganises = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-reservations")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UtilisateurBadge> badges = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

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

    public enum NiveauFidelite {
        BRONZE("Bronze"),
        ARGENT("Argent"),
        OR("Or"),
        DIAMANT("Diamant");

        private final String libelle;

        NiveauFidelite(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }

    public boolean isAdmin() {
        return this.role == RoleUtilisateur.ADMIN;
    }

    public boolean isOrganisateur() {
        return this.role == RoleUtilisateur.ORGANISATEUR || this.role == RoleUtilisateur.ADMIN;
    }

    // Constructeurs
    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public RoleUtilisateur getRole() { return role; }
    public void setRole(RoleUtilisateur role) { this.role = role; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public List<Evenement> getEvenementsOrganises() { return evenementsOrganises; }
    public void setEvenementsOrganises(List<Evenement> evenementsOrganises) { this.evenementsOrganises = evenementsOrganises; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    public List<UtilisateurBadge> getBadges() { return badges; }
    public void setBadges(List<UtilisateurBadge> badges) { this.badges = badges; }
    public Integer getPointsFidelite() { return pointsFidelite; }
    public void setPointsFidelite(Integer pointsFidelite) { this.pointsFidelite = pointsFidelite; }
    public NiveauFidelite getNiveauFidelite() { return niveauFidelite; }
    public void setNiveauFidelite(NiveauFidelite niveauFidelite) { this.niveauFidelite = niveauFidelite; }
    public Integer getTotalReservations() { return totalReservations; }
    public void setTotalReservations(Integer totalReservations) { this.totalReservations = totalReservations; }
    public Integer getTotalEvenementsOrganises() { return totalEvenementsOrganises; }
    public void setTotalEvenementsOrganises(Integer totalEvenementsOrganises) { this.totalEvenementsOrganises = totalEvenementsOrganises; }
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }

    public void ajouterPoints(int points) {
        this.pointsFidelite += points;
        this.mettreAJourNiveauFidelite();
    }

    public void incrementerReservations() {
        this.totalReservations++;
    }

    public void incrementerEvenementsOrganises() {
        this.totalEvenementsOrganises++;
    }

    private void mettreAJourNiveauFidelite() {
        if (this.pointsFidelite >= 1000) {
            this.niveauFidelite = NiveauFidelite.DIAMANT;
        } else if (this.pointsFidelite >= 500) {
            this.niveauFidelite = NiveauFidelite.OR;
        } else if (this.pointsFidelite >= 100) {
            this.niveauFidelite = NiveauFidelite.ARGENT;
        } else {
            this.niveauFidelite = NiveauFidelite.BRONZE;
        }
    }
}