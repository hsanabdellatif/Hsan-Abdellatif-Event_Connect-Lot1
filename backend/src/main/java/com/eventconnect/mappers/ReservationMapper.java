package com.eventconnect.mappers;

import com.eventconnect.dto.ReservationDTO;
import com.eventconnect.entities.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre Reservation et ReservationDTO
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Component
public class ReservationMapper {

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    @Autowired
    private EvenementMapper evenementMapper;

    /**
     * Convertit une entité Reservation en DTO simple (avec IDs)
     * @param reservation l'entité à convertir
     * @return le DTO correspondant
     */
    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return new ReservationDTO(
            reservation.getId(),
            reservation.getNombrePlaces(),
            reservation.getMontantTotal(),
            reservation.getStatut(),
            reservation.getDateReservation(),
            reservation.getDateConfirmation(),
            reservation.getDateAnnulation(),
            reservation.getCommentaire(),
            reservation.getUtilisateur() != null ? reservation.getUtilisateur().getId() : null,
            reservation.getUtilisateur() != null ? reservation.getUtilisateur().getNomComplet() : null,
            reservation.getUtilisateur() != null ? reservation.getUtilisateur().getEmail() : null,
            reservation.getEvenement() != null ? reservation.getEvenement().getId() : null,
            reservation.getEvenement() != null ? reservation.getEvenement().getTitre() : null,
            reservation.getEvenement() != null ? reservation.getEvenement().getDateDebut() : null
        );
    }

    /**
     * Convertit une entité Reservation en DTO complet (avec DTOs imbriqués)
     * @param reservation l'entité à convertir
     * @return le DTO avec toutes les informations
     */
    public ReservationDTO toFullDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        return new ReservationDTO(
            reservation.getId(),
            reservation.getNombrePlaces(),
            reservation.getMontantTotal(),
            reservation.getStatut(),
            reservation.getDateReservation(),
            reservation.getDateConfirmation(),
            reservation.getDateAnnulation(),
            reservation.getCommentaire(),
            reservation.getUtilisateur() != null ? utilisateurMapper.toSimpleDTO(reservation.getUtilisateur()) : null,
            reservation.getEvenement() != null ? evenementMapper.toSimpleDTO(reservation.getEvenement()) : null
        );
    }

    /**
     * Convertit un DTO en entité Reservation
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setNombrePlaces(dto.getNombrePlaces());
        reservation.setMontantTotal(dto.getMontantTotal());
        reservation.setStatut(dto.getStatut());
        reservation.setDateReservation(dto.getDateReservation());
        reservation.setDateConfirmation(dto.getDateConfirmation());
        reservation.setDateAnnulation(dto.getDateAnnulation());
        reservation.setCommentaire(dto.getCommentaires());

        // Note: Les entités utilisateur et événement doivent être définies séparément
        // car elles nécessitent un accès aux repositories
        
        return reservation;
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     * @param reservation l'entité à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     */
    public void updateEntityFromDTO(Reservation reservation, ReservationDTO dto) {
        if (reservation == null || dto == null) {
            return;
        }

        reservation.setNombrePlaces(dto.getNombrePlaces());
        reservation.setMontantTotal(dto.getMontantTotal());
        reservation.setStatut(dto.getStatut());
        reservation.setDateConfirmation(dto.getDateConfirmation());
        reservation.setDateAnnulation(dto.getDateAnnulation());
        
        if (dto.getCommentaires() != null) {
            reservation.setCommentaire(dto.getCommentaires());
        }
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     * @param reservations la liste d'entités
     * @return la liste de DTOs
     */
    public List<ReservationDTO> toDTOList(List<Reservation> reservations) {
        if (reservations == null) {
            return null;
        }

        return reservations.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convertit une liste d'entités en liste de DTOs complets
     * @param reservations la liste d'entités
     * @return la liste de DTOs complets
     */
    public List<ReservationDTO> toFullDTOList(List<Reservation> reservations) {
        if (reservations == null) {
            return null;
        }

        return reservations.stream()
            .map(this::toFullDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de DTOs en liste d'entités
     * @param dtos la liste de DTOs
     * @return la liste d'entités
     */
    public List<Reservation> toEntityList(List<ReservationDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    /**
     * Crée un DTO minimaliste pour les listes
     * @param reservation l'entité
     * @return DTO simplifié
     */
    public ReservationDTO toMinimalDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setNombrePlaces(reservation.getNombrePlaces());
        dto.setMontantTotal(reservation.getMontantTotal());
        dto.setStatut(reservation.getStatut());
        dto.setDateReservation(reservation.getDateReservation());
        
        if (reservation.getEvenement() != null) {
            dto.setEvenementTitre(reservation.getEvenement().getTitre());
            dto.setEvenementDate(reservation.getEvenement().getDateDebut());
        }

        return dto;
    }
}
