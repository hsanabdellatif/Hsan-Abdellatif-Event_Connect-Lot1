package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un événement dans l'application EventConnect
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Entity
@Table(name = "evenements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @NotBlank(message = "Le lieu est obligatoire")
    @Size(min = 3, max = 200, message = "Le lieu doit contenir entre 3 et 200 caractères")
    @Column(nullable = false, length = 200)
    private String lieu;

    @NotNull(message = "Le nombre de places maximum est obligatoire")
    @Min(value = 1, message = "Le nombre de places maximum doit être au moins 1")
    @Max(value = 10000, message = "Le nombre de places maximum ne peut pas dépasser 10000")
    @Column(name = "places_max", nullable = false)
    private Integer placesMax;

    @Column(name = "places_disponibles", nullable = false)
    private Integer placesDisponibles;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix ne peut pas être négatif")
    @Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au maximum 8 chiffres avant la virgule et 2 après")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieEvenement categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEvenement statut = StatutEvenement.PLANIFIE;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "actif")
    private Boolean actif = true;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisateur_id", nullable = false)
    @NotNull(message = "L'organisateur est obligatoire")
    private Utilisateur organisateur;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Méthode appelée avant la mise à jour de l'entité
     */
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Méthode appelée avant la persistance de l'entité
     */
    @PrePersist
    public void prePersist() {
        if (this.placesDisponibles == null) {
            this.placesDisponibles = this.placesMax;
        }
    }

    /**
     * Validation personnalisée pour s'assurer que la date de fin est après la date de début
     */
    @AssertTrue(message = "La date de fin doit être après la date de début")
    public boolean isDateFinValid() {
        return dateFin == null || dateDebut == null || dateFin.isAfter(dateDebut);
    }

    /**
     * Enum pour les catégories d'événements
     */
    public enum CategorieEvenement {
        CONFERENCE("Conférence"),
        WORKSHOP("Atelier"),
        NETWORKING("Networking"),
        FORMATION("Formation"),
        SEMINAIRE("Séminaire"),
        CONCERT("Concert"),
        SPORT("Sport"),
        CULTUREL("Culturel"),
        AUTRE("Autre");

        private final String libelle;

        CategorieEvenement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    /**
     * Enum pour les statuts d'événements
     */
    public enum StatutEvenement {
        PLANIFIE("Planifié"),
        EN_COURS("En cours"),
        TERMINE("Terminé"),
        ANNULE("Annulé"),
        REPORTE("Reporté");

        private final String libelle;

        StatutEvenement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    /**
     * Méthode utilitaire pour vérifier si l'événement est complet
     */
    public boolean isComplet() {
        return this.placesDisponibles <= 0;
    }

    /**
     * Méthode utilitaire pour vérifier si l'événement est ouvert aux réservations
     */
    public boolean isOuvertAuxReservations() {
        return this.actif && 
               this.statut == StatutEvenement.PLANIFIE && 
               !isComplet() && 
               this.dateDebut.isAfter(LocalDateTime.now());
    }

    /**
     * Méthode utilitaire pour diminuer le nombre de places disponibles
     */
    public void diminuerPlacesDisponibles(int nombre) {
        if (this.placesDisponibles >= nombre) {
            this.placesDisponibles -= nombre;
        } else {
            throw new IllegalStateException("Pas assez de places disponibles");
        }
    }

    /**
     * Méthode utilitaire pour augmenter le nombre de places disponibles
     */
    public void augmenterPlacesDisponibles(int nombre) {
        this.placesDisponibles = Math.min(this.placesDisponibles + nombre, this.placesMax);
    }
}
