package com.eventconnect.dto;

import com.eventconnect.entities.Reservation.StatutReservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour l'entité Reservation
 * Utilisé pour les transferts de données dans les APIs
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDTO {

    private Long id;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 1, message = "Le nombre de places doit être au moins 1")
    private Integer nombrePlaces;

    private BigDecimal montantTotal;

    private StatutReservation statut;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateReservation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateConfirmation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateAnnulation;

    private String commentaires;

    // Informations sur l'utilisateur (nested DTO)
    private UtilisateurDTO utilisateur;

    // Informations sur l'événement (nested DTO)
    private EvenementDTO evenement;

    // Informations simplifiées pour les listes
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurEmail;

    private Long evenementId;
    private String evenementTitre;
    private LocalDateTime evenementDate;

    /**
     * Constructeur pour la création de réservation
     */
    public ReservationDTO(Long utilisateurId, Long evenementId, Integer nombrePlaces) {
        this.utilisateurId = utilisateurId;
        this.evenementId = evenementId;
        this.nombrePlaces = nombrePlaces;
    }

    /**
     * Constructeur complet pour la lecture
     */
    public ReservationDTO(Long id, Integer nombrePlaces, BigDecimal montantTotal, StatutReservation statut,
                         LocalDateTime dateReservation, LocalDateTime dateConfirmation, LocalDateTime dateAnnulation,
                         String commentaires, Long utilisateurId, String utilisateurNom, String utilisateurEmail,
                         Long evenementId, String evenementTitre, LocalDateTime evenementDate) {
        this.id = id;
        this.nombrePlaces = nombrePlaces;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.dateReservation = dateReservation;
        this.dateConfirmation = dateConfirmation;
        this.dateAnnulation = dateAnnulation;
        this.commentaires = commentaires;
        this.utilisateurId = utilisateurId;
        this.utilisateurNom = utilisateurNom;
        this.utilisateurEmail = utilisateurEmail;
        this.evenementId = evenementId;
        this.evenementTitre = evenementTitre;
        this.evenementDate = evenementDate;
    }

    /**
     * Constructeur avec DTOs complets
     */
    public ReservationDTO(Long id, Integer nombrePlaces, BigDecimal montantTotal, StatutReservation statut,
                         LocalDateTime dateReservation, LocalDateTime dateConfirmation, LocalDateTime dateAnnulation,
                         String commentaires, UtilisateurDTO utilisateur, EvenementDTO evenement) {
        this.id = id;
        this.nombrePlaces = nombrePlaces;
        this.montantTotal = montantTotal;
        this.statut = statut;
        this.dateReservation = dateReservation;
        this.dateConfirmation = dateConfirmation;
        this.dateAnnulation = dateAnnulation;
        this.commentaires = commentaires;
        this.utilisateur = utilisateur;
        this.evenement = evenement;
    }

    /**
     * Vérifie si la réservation peut être annulée
     * @return true si annulable
     */
    public boolean isAnnulable() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRMEE;
    }

    /**
     * Vérifie si la réservation peut être confirmée
     * @return true si confirmable
     */
    public boolean isConfirmable() {
        return statut == StatutReservation.EN_ATTENTE;
    }

    /**
     * Calcule le prix unitaire par place
     * @return prix par place
     */
    public BigDecimal getPrixParPlace() {
        if (montantTotal != null && nombrePlaces != null && nombrePlaces > 0) {
            return montantTotal.divide(new BigDecimal(nombrePlaces), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Retourne un résumé de la réservation
     * @return résumé
     */
    public String getResume() {
        return String.format("Réservation #%d - %s places pour %s (%s)", 
                id, nombrePlaces, evenementTitre, statut);
    }

    /**
     * Vérifie si la réservation est active
     * @return true si active
     */
    public boolean isActive() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRMEE;
    }
}
