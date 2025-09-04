package com.eventconnect.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Evenement
 * Utilisé pour les transferts de données dans les APIs
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
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
    @Pattern(regexp = "CONFERENCE|WORKSHOP|NETWORKING|FORMATION|SEMINAIRE|CONCERT|SPORT|CULTUREL|AUTRE",
            message = "Catégorie invalide")
    private String categorie;

    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModification;

    private Boolean actif;

    private Integer placesReservees;
    private Integer placesDisponibles;
    private BigDecimal chiffreAffaires;

    public EvenementDTO() {}

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public Integer getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(Integer capaciteMax) { this.capaciteMax = capaciteMax; }

    public BigDecimal getPrixPlace() { return prixPlace; }
    public void setPrixPlace(BigDecimal prixPlace) { this.prixPlace = prixPlace; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Integer getPlacesReservees() { return placesReservees; }
    public void setPlacesReservees(Integer placesReservees) { this.placesReservees = placesReservees; }

    public Integer getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(Integer placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    public BigDecimal getChiffreAffaires() { return chiffreAffaires; }
    public void setChiffreAffaires(BigDecimal chiffreAffaires) { this.chiffreAffaires = chiffreAffaires; }

    public long getDureeEnHeures() {
        if (dateDebut != null && dateFin != null) {
            return java.time.Duration.between(dateDebut, dateFin).toHours();
        }
        return 0;
    }

    public boolean isComplet() {
        return placesDisponibles != null && placesDisponibles <= 0;
    }

    public double getTauxRemplissage() {
        if (capaciteMax != null && placesReservees != null && capaciteMax > 0) {
            return (placesReservees * 100.0) / capaciteMax;
        }
        return 0.0;
    }
}