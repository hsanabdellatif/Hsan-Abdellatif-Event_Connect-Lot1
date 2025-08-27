package com.eventconnect.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour l'entité Utilisateur
 * Utilisé pour les transferts de données dans les APIs
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private String telephone;
    private LocalDateTime dateInscription;
    private LocalDateTime dateModification;
    private Boolean actif;

    /**
     * Constructeur pour la création (sans ID)
     */
    public UtilisateurDTO(String nom, String prenom, String email, String motDePasse, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
    }

    /**
     * Constructeur pour la lecture (sans mot de passe)
     */
    public UtilisateurDTO(Long id, String nom, String prenom, String email, String telephone, 
                         LocalDateTime dateInscription, LocalDateTime dateModification, Boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateInscription = dateInscription;
        this.dateModification = dateModification;
        this.actif = actif;
    }

    /**
     * Retourne le nom complet de l'utilisateur
     * @return nom complet
     */
    public String getNomComplet() {
        return this.prenom + " " + this.nom;
    }
}
