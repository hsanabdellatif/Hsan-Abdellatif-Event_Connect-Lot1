package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entité représentant une réponse à une enquête de satisfaction
 * Associe un utilisateur à ses réponses pour une enquête spécifique
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@Entity
@Table(name = "reponses_enquete",
       uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "enquete_id"}))
public class ReponseEnquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur; // Nullable pour réponses anonymes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquete_id", nullable = false)
    @NotNull(message = "L'enquête est obligatoire")
    private EnqueteSatisfaction enquete;

    @Column(name = "reponses", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Les réponses sont obligatoires")
    private String reponses; // JSON des réponses structurées

    @Column(name = "date_reponse", nullable = false)
    @NotNull
    private LocalDateTime dateReponse;

    @Column(name = "anonyme", nullable = false)
    private Boolean anonyme = false;

    @Column(name = "complete", nullable = false)
    private Boolean complete = false;

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;

    @Column(name = "duree_reponse_secondes")
    private Integer dureeReponseSecondes;

    /**
     * Constructeur par défaut
     */
    public ReponseEnquete() {
        this.dateReponse = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres principaux
     * 
     * @param utilisateur l'utilisateur (nullable si anonyme)
     * @param enquete l'enquête concernée
     * @param reponses les réponses en format JSON
     * @param anonyme si la réponse est anonyme
     */
    public ReponseEnquete(Utilisateur utilisateur, EnqueteSatisfaction enquete, 
                         String reponses, Boolean anonyme) {
        this();
        this.utilisateur = utilisateur;
        this.enquete = enquete;
        this.reponses = reponses;
        this.anonyme = anonyme;
    }

    /**
     * Méthode appelée avant la persistance
     */
    @PrePersist
    public void prePersist() {
        this.dateReponse = LocalDateTime.now();
        this.anonyme = false;
        this.complete = false;
    }

    /**
     * Marque la réponse comme complète
     */
    public void marquerComplete() {
        this.complete = true;
    }

    /**
     * Vérifie si la réponse est valide
     * 
     * @return true si la réponse est valide
     */
    public boolean isValide() {
        return this.reponses != null && 
               !this.reponses.trim().isEmpty() && 
               this.enquete.isActiveEtValide();
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public EnqueteSatisfaction getEnquete() {
        return enquete;
    }

    public void setEnquete(EnqueteSatisfaction enquete) {
        this.enquete = enquete;
    }

    public String getReponses() {
        return reponses;
    }

    public void setReponses(String reponses) {
        this.reponses = reponses;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    public Boolean getAnonyme() {
        return anonyme;
    }

    public void setAnonyme(Boolean anonyme) {
        this.anonyme = anonyme;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getAdresseIp() {
        return adresseIp;
    }

    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }

    public Integer getDureeReponseSecondes() {
        return dureeReponseSecondes;
    }

    public void setDureeReponseSecondes(Integer dureeReponseSecondes) {
        this.dureeReponseSecondes = dureeReponseSecondes;
    }

    @Override
    public String toString() {
        return String.format("ReponseEnquete{id=%d, utilisateur=%s, enquete=%s, anonyme=%s, complete=%s}", 
                           id, 
                           anonyme ? "Anonyme" : (utilisateur != null ? utilisateur.getEmail() : "null"),
                           enquete != null ? enquete.getTitre() : "null",
                           anonyme, 
                           complete);
    }
}
