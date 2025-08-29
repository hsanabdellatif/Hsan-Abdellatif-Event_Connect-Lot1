package com.eventconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entité représentant une enquête de satisfaction pour un événement
 * Permet de recueillir des feedbacks structurés post-événement
 * 
 * @author EventConnect Team
 * @since 2.0.0
 */
@Entity
@Table(name = "enquetes_satisfaction")
public class EnqueteSatisfaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    @NotNull(message = "L'événement est obligatoire")
    private Evenement evenement;

    @Column(name = "titre", nullable = false, length = 200)
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @Column(name = "date_creation", nullable = false)
    @NotNull
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "anonyme_autorise", nullable = false)
    private Boolean anonymeAutorise = true;

    @Column(name = "obligatoire", nullable = false)
    private Boolean obligatoire = false;

    @Column(name = "questions", columnDefinition = "TEXT")
    private String questions; // JSON des questions structurées

    @OneToMany(mappedBy = "enquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponseEnquete> reponses;

    /**
     * Constructeur par défaut
     */
    public EnqueteSatisfaction() {
        this.dateCreation = LocalDateTime.now();
        this.dateExpiration = LocalDateTime.now().plusDays(30); // 30 jours par défaut
    }

    /**
     * Constructeur avec paramètres principaux
     * 
     * @param evenement l'événement concerné
     * @param titre le titre de l'enquête
     * @param description la description
     */
    public EnqueteSatisfaction(Evenement evenement, String titre, String description) {
        this();
        this.evenement = evenement;
        this.titre = titre;
        this.description = description;
    }

    /**
     * Méthode appelée avant la persistance
     */
    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.dateExpiration == null) {
            this.dateExpiration = LocalDateTime.now().plusDays(30);
        }
        this.active = true;
        this.anonymeAutorise = true;
        this.obligatoire = false;
    }

    /**
     * Vérifie si l'enquête est encore active
     * 
     * @return true si l'enquête est active et non expirée
     */
    public boolean isActiveEtValide() {
        return this.active && 
               (this.dateExpiration == null || this.dateExpiration.isAfter(LocalDateTime.now()));
    }

    /**
     * Active l'enquête
     */
    public void activer() {
        this.active = true;
    }

    /**
     * Désactive l'enquête
     */
    public void desactiver() {
        this.active = false;
    }

    /**
     * Prolonge la durée de l'enquête
     * 
     * @param jours nombre de jours à ajouter
     */
    public void prolonger(int jours) {
        if (this.dateExpiration != null) {
            this.dateExpiration = this.dateExpiration.plusDays(jours);
        } else {
            this.dateExpiration = LocalDateTime.now().plusDays(jours);
        }
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAnonymeAutorise() {
        return anonymeAutorise;
    }

    public void setAnonymeAutorise(Boolean anonymeAutorise) {
        this.anonymeAutorise = anonymeAutorise;
    }

    public Boolean getObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(Boolean obligatoire) {
        this.obligatoire = obligatoire;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public List<ReponseEnquete> getReponses() {
        return reponses;
    }

    public void setReponses(List<ReponseEnquete> reponses) {
        this.reponses = reponses;
    }

    @Override
    public String toString() {
        return String.format("EnqueteSatisfaction{id=%d, titre='%s', evenement=%s, active=%s}", 
                           id, titre, 
                           evenement != null ? evenement.getTitre() : "null",
                           active);
    }
}
