package com.eventconnect.dto;

import com.eventconnect.entities.Utilisateur;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO pour l'entité Utilisateur
 * Utilisé pour les transferts de données dans les APIs
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtilisateurDTO {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    // Mot de passe uniquement pour la création/modification
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @Size(max = 15, message = "Le téléphone ne peut pas dépasser 15 caractères")
    private String telephone;

    private LocalDateTime dateInscription;
    private LocalDateTime dateModification;
    private Boolean actif;

    private Utilisateur.RoleUtilisateur role;
    private Integer pointsFidelite;
    private Utilisateur.NiveauFidelite niveauFidelite;
    private Integer totalReservations;
    private Integer totalEvenementsOrganises;
    private Double totalDepense; // Ajout pour correspondre au frontend

    // Constructeurs
    public UtilisateurDTO() {}

    public UtilisateurDTO(Long id, String nom, String prenom, String email, String telephone,
                          LocalDateTime dateInscription, LocalDateTime dateModification, Boolean actif,
                          Utilisateur.RoleUtilisateur role, Integer pointsFidelite,
                          Utilisateur.NiveauFidelite niveauFidelite, Integer totalReservations,
                          Integer totalEvenementsOrganises, Double totalDepense) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateInscription = dateInscription;
        this.dateModification = dateModification;
        this.actif = actif;
        this.role = role;
        this.pointsFidelite = pointsFidelite;
        this.niveauFidelite = niveauFidelite;
        this.totalReservations = totalReservations;
        this.totalEvenementsOrganises = totalEvenementsOrganises;
        this.totalDepense = totalDepense;
    }

    /**
     * Retourne le nom complet de l'utilisateur
     * @return nom complet
     */
    public String getNomComplet() {
        if (prenom != null && nom != null) {
            return prenom + " " + nom;
        }
        return nom != null ? nom : "";
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Utilisateur.RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(Utilisateur.RoleUtilisateur role) {
        this.role = role;
    }

    public Integer getPointsFidelite() {
        return pointsFidelite;
    }

    public void setPointsFidelite(Integer pointsFidelite) {
        this.pointsFidelite = pointsFidelite;
    }

    public Utilisateur.NiveauFidelite getNiveauFidelite() {
        return niveauFidelite;
    }

    public void setNiveauFidelite(Utilisateur.NiveauFidelite niveauFidelite) {
        this.niveauFidelite = niveauFidelite;
    }

    public Integer getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(Integer totalReservations) {
        this.totalReservations = totalReservations;
    }

    public Integer getTotalEvenementsOrganises() {
        return totalEvenementsOrganises;
    }

    public void setTotalEvenementsOrganises(Integer totalEvenementsOrganises) {
        this.totalEvenementsOrganises = totalEvenementsOrganises;
    }

    public Double getTotalDepense() {
        return totalDepense;
    }

    public void setTotalDepense(Double totalDepense) {
        this.totalDepense = totalDepense;
    }
}