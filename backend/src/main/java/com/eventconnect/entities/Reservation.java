package com.eventconnect.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    @NotNull(message = "L'utilisateur est obligatoire")
    @JsonIgnore // Changement pour éviter la récursion
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    @NotNull(message = "L'événement est obligatoire")
    @JsonIgnore // Changement pour éviter la récursion
    private Evenement evenement;

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

    @PrePersist
    public void prePersist() {
        if (this.codeReservation == null) {
            this.codeReservation = genererCodeReservation();
        }
        calculerMontantTotal();
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

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

    private String genererCodeReservation() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void calculerMontantTotal() {
        if (this.evenement != null && this.nombrePlaces != null) {
            this.montantTotal = this.evenement.getPrix().multiply(new BigDecimal(this.nombrePlaces));
        }
    }

    public void confirmer() {
        if (this.statut == StatutReservation.EN_ATTENTE) {
            this.statut = StatutReservation.CONFIRMEE;
            this.dateConfirmation = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Seules les réservations en attente peuvent être confirmées");
        }
    }

    public void annuler() {
        if (this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE) {
            this.statut = StatutReservation.ANNULEE;
            this.dateAnnulation = LocalDateTime.now();
            if (this.evenement != null) {
                this.evenement.augmenterPlacesDisponibles(this.nombrePlaces);
            }
        } else {
            throw new IllegalStateException("Cette réservation ne peut pas être annulée");
        }
    }

    public boolean isModifiable() {
        return this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE;
    }

    public boolean isActive() {
        return this.actif &&
                (this.statut == StatutReservation.EN_ATTENTE || this.statut == StatutReservation.CONFIRMEE);
    }

    public boolean peutEtreRemboursee() {
        return this.statut == StatutReservation.CONFIRMEE &&
                this.evenement.getDateDebut().isAfter(LocalDateTime.now().plusHours(24));
    }

    public void marquerCommeRemboursee() {
        if (peutEtreRemboursee()) {
            this.statut = StatutReservation.REMBOURSEE;
            this.dateModification = LocalDateTime.now();
            if (this.evenement != null) {
                this.evenement.augmenterPlacesDisponibles(this.nombrePlaces);
            }
        } else {
            throw new IllegalStateException("Cette réservation ne peut pas être remboursée");
        }
    }
}