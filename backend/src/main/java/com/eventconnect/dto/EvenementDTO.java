package com.eventconnect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Evenement
 * Utilisé pour les transferts de données dans les APIs
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvenementDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    @Size(min = 3, max = 200, message = "Le lieu doit contenir entre 3 et 200 caractères")
    private String lieu;

    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Max(value = 10000, message = "La capacité ne peut pas dépasser 10000")
    private Integer capaciteMax;

    @NotNull(message = "Le prix par place est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul")
    @Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au maximum 8 chiffres avant la virgule et 2 après")
    private BigDecimal prixPlace;

    @NotBlank(message = "La catégorie est obligatoire")
    @Size(min = 2, max = 50, message = "La catégorie doit contenir entre 2 et 50 caractères")
    private String categorie;

    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModification;

    private Boolean actif;

    // Informations calculées (lecture seule)
    private Integer placesReservees;
    private Integer placesDisponibles;
    private BigDecimal chiffreAffaires;

    /**
     * Constructeur pour la création (sans ID et sans champs calculés)
     */
    public EvenementDTO(String titre, String description, LocalDateTime dateDebut, LocalDateTime dateFin,
                       String lieu, Integer capaciteMax, BigDecimal prixPlace, String categorie, String imageUrl) {
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
        this.prixPlace = prixPlace;
        this.categorie = categorie;
        this.imageUrl = imageUrl;
    }

    /**
     * Constructeur pour la lecture complète
     */
    public EvenementDTO(Long id, String titre, String description, LocalDateTime dateDebut, LocalDateTime dateFin,
                       String lieu, Integer capaciteMax, BigDecimal prixPlace, String categorie, String imageUrl,
                       LocalDateTime dateCreation, LocalDateTime dateModification, Boolean actif,
                       Integer placesReservees, Integer placesDisponibles, BigDecimal chiffreAffaires) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
        this.prixPlace = prixPlace;
        this.categorie = categorie;
        this.imageUrl = imageUrl;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.actif = actif;
        this.placesReservees = placesReservees;
        this.placesDisponibles = placesDisponibles;
        this.chiffreAffaires = chiffreAffaires;
    }

    /**
     * Calcule la durée de l'événement en heures
     * @return durée en heures
     */
    public long getDureeEnHeures() {
        if (dateDebut != null && dateFin != null) {
            return java.time.Duration.between(dateDebut, dateFin).toHours();
        }
        return 0;
    }

    /**
     * Vérifie si l'événement est complet
     * @return true si complet
     */
    public boolean isComplet() {
        return placesDisponibles != null && placesDisponibles <= 0;
    }

    /**
     * Calcule le taux de remplissage en pourcentage
     * @return taux de remplissage
     */
    public double getTauxRemplissage() {
        if (capaciteMax != null && placesReservees != null && capaciteMax > 0) {
            return (placesReservees * 100.0) / capaciteMax;
        }
        return 0.0;
    }
}
