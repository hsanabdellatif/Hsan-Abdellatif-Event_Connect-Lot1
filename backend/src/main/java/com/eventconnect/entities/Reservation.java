package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une réservation d'événement dans l'application EventConnect
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@Entity
@Table(name = "reservations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "evenement_id"}))
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

    @NotNull(message = "Le montant total est obligatoire")
    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @Column(name = "date_reservation", nullable = false)
    private LocalDateTime dateReservation = LocalDateTime.now();

    @Column(name = "date_confirmation")
    private LocalDateTime dateConfirmation;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "code_reservation", unique = true, length = 20)
    private String codeReservation;

    @Column(length = 500)
    private String commentaire;

    @Column(name = "actif")
    private Boolean actif = true;

    // Relations
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evenement_id", nullable = false)
    @NotNull(message = "L'événement est obligatoire")
    private Evenement evenement;

    // Constructeurs
    public Reservation() {}

    public Reservation(Long id, Integer nombrePlaces, BigDecimal montantTotal,
                       StatutReservation statut, LocalDateTime dateReservation,
                       LocalDateTime dateConfirmation, LocalDateTime dateAnnulation,
                       LocalDateTime dateModification, String codeReservation,
                       String commentaire, Boolean actif, Utilisateur utilisateur,
                       Evenement evenement) {
        this.id = id;
        this.nombrePlaces = nombrePlaces;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.dateReservation = dateReservation;
        this.dateConfirmation = dateConfirmation;
        this.dateAnnulation = dateAnnulation;
        this.dateModification = dateModification;
        this.codeReservation = codeReservation;
        this.commentaire = commentaire;
        this.actif = actif;
        this.utilisateur = utilisateur;
        this.evenement = evenement;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(Integer nombrePlaces) { this.nombrePlaces = nombrePlaces; }

    public BigDecimal getMontantTotal() { return montantTotal; }
    public void setMontantTotal(BigDecimal montantTotal) { this.montantTotal = montantTotal; }

    public StatutReservation getStatut() { return statut; }
    public void setStatut(StatutReservation statut) { this.statut = statut; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

    public LocalDateTime getDateConfirmation() { return dateConfirmation; }
    public void setDateConfirmation(LocalDateTime dateConfirmation) { this.dateConfirmation = dateConfirmation; }

    public LocalDateTime getDateAnnulation() { return dateAnnulation; }
    public void setDateAnnulation(LocalDateTime dateAnnulation) { this.dateAnnulation = dateAnnulation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public String getCodeReservation() { return codeReservation; }
    public void setCodeReservation(String codeReservation) { this.codeReservation = codeReservation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    /**
     * Méthode appelée avant la persistance de l'entité
     */
    @PrePersist
    public void prePersist() {
        if (this.codeReservation == null) {
            this.codeReservation = genererCodeReservation();
        }
        calculerMontantTotal();
    }

    /**
     * Méthode appelée avant la mise à jour de l'entité
     */
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Enum pour les statuts de réservation
     */
    public enum StatutReservation {
        EN_ATTENTE("En attente"),
        CONFIRMEE("Confirmée"),
        ANNULEE("Annulée"),
        EXPIREE("Expirée"),
        REMBOURSEE("Remboursée");

        private final String libelle;

        StatutReservation(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    /**
     * Génère un code de réservation unique
     */
    private String genererCodeReservation() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = timestamp.substring(timestamp.length() - 8);
        return "RES" + suffix;
    }

    /**
     * Calcule le montant total de la réservation
     */
    private void calculerMontantTotal() {
        if (this.evenement != null && this.nombrePlaces != null) {
            this.montantTotal = this.evenement.getPrix().multiply(new BigDecimal(this.nombrePlaces));
        }
    }

    /**
     * Méthode pour confirmer la réservation
     */
    public void confirmer() {
        if (this.statut == StatutReservation.EN_ATTENTE) {
            this.statut = StatutReservation.CONFIRMEE;
            this.dateConfirmation = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Seules les réservations en attente peuvent être confirmées");
        }
    }

    /**
     * Méthode pour annuler la réservation
     */
    public void annuler() {
        if (this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE) {
            this.statut = StatutReservation.ANNULEE;
            this.dateAnnulation = LocalDateTime.now();

            // Libérer les places dans l'événement
            if (this.evenement != null) {
                this.evenement.augmenterPlacesDisponibles(this.nombrePlaces);
            }
        } else {
            throw new IllegalStateException("Cette réservation ne peut pas être annulée");
        }
    }

    /**
     * Méthode utilitaire pour vérifier si la réservation est modifiable
     */
    public boolean isModifiable() {
        return this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE;
    }

    /**
     * Méthode utilitaire pour vérifier si la réservation est active
     */
    public boolean isActive() {
        return this.actif &&
                (this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE);
    }

    /**
     * Méthode utilitaire pour vérifier si la réservation peut être remboursée
     */
    public boolean peutEtreRemboursee() {
        return this.statut == StatutReservation.CONFIRMEE &&
                this.evenement.getDateDebut().isAfter(LocalDateTime.now().plusHours(24));
    }

    /**
     * Méthode pour marquer comme remboursée
     */
    public void marquerCommeRemboursee() {
        if (peutEtreRemboursee()) {
            this.statut = StatutReservation.REMBOURSEE;
            this.dateModification = LocalDateTime.now();

            // Libérer les places dans l'événement
            if (this.evenement != null) {
                this.evenement.augmenterPlacesDisponibles(this.nombrePlaces);
            }
        } else {
            throw new IllegalStateException("Cette réservation ne peut pas être remboursée");
        }
    }
}