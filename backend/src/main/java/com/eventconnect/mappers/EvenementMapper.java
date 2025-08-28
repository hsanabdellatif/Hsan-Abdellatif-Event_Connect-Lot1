package com.eventconnect.mappers;

import com.eventconnect.dto.EvenementDTO;
import com.eventconnect.entities.Evenement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre Evenement et EvenementDTO
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Component
public class EvenementMapper {

    /**
     * Convertit une entité Evenement en DTO
     * @param evenement l'entité à convertir
     * @return le DTO correspondant
     */
    public EvenementDTO toDTO(Evenement evenement) {
        if (evenement == null) {
            return null;
        }

        return new EvenementDTO(
            evenement.getId(),
            evenement.getTitre(),
            evenement.getDescription(),
            evenement.getDateDebut(),
            evenement.getDateFin(),
            evenement.getLieu(),
            evenement.getCapaciteMax(),
            evenement.getPrixPlace(),
            evenement.getCategorie(),
            evenement.getImageUrl(),
            evenement.getDateCreation(),
            evenement.getDateModification(),
            evenement.getActif(),
            null, // placesReservees - sera calculé si nécessaire
            null, // placesDisponibles - sera calculé si nécessaire
            null  // chiffreAffaires - sera calculé si nécessaire
        );
    }

    /**
     * Convertit une entité Evenement en DTO avec informations calculées
     * @param evenement l'entité à convertir
     * @param placesReservees nombre de places réservées
     * @param chiffreAffaires chiffre d'affaires de l'événement
     * @return le DTO avec toutes les informations
     */
    public EvenementDTO toDTOWithStats(Evenement evenement, Integer placesReservees, java.math.BigDecimal chiffreAffaires) {
        if (evenement == null) {
            return null;
        }

        Integer placesDisponibles = evenement.getCapaciteMax() - (placesReservees != null ? placesReservees : 0);

        return new EvenementDTO(
            evenement.getId(),
            evenement.getTitre(),
            evenement.getDescription(),
            evenement.getDateDebut(),
            evenement.getDateFin(),
            evenement.getLieu(),
            evenement.getCapaciteMax(),
            evenement.getPrixPlace(),
            evenement.getCategorie(),
            evenement.getImageUrl(),
            evenement.getDateCreation(),
            evenement.getDateModification(),
            evenement.getActif(),
            placesReservees,
            placesDisponibles,
            chiffreAffaires
        );
    }

    /**
     * Convertit un DTO en entité Evenement
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    public Evenement toEntity(EvenementDTO dto) {
        if (dto == null) {
            return null;
        }

        Evenement evenement = new Evenement();
        evenement.setId(dto.getId());
        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setDateDebut(dto.getDateDebut());
        evenement.setDateFin(dto.getDateFin());
        evenement.setLieu(dto.getLieu());
        evenement.setCapaciteMax(dto.getCapaciteMax());
        evenement.setPrixPlace(dto.getPrixPlace());
        evenement.setCategorie(dto.getCategorie());
        evenement.setImageUrl(dto.getImageUrl());
        evenement.setDateCreation(dto.getDateCreation());
        evenement.setDateModification(dto.getDateModification());
        evenement.setActif(dto.getActif());

        return evenement;
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     * @param evenement l'entité à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     */
    public void updateEntityFromDTO(Evenement evenement, EvenementDTO dto) {
        if (evenement == null || dto == null) {
            return;
        }

        evenement.setTitre(dto.getTitre());
        evenement.setDescription(dto.getDescription());
        evenement.setDateDebut(dto.getDateDebut());
        evenement.setDateFin(dto.getDateFin());
        evenement.setLieu(dto.getLieu());
        evenement.setCapaciteMax(dto.getCapaciteMax());
        evenement.setPrixPlace(dto.getPrixPlace());
        evenement.setCategorie(dto.getCategorie());
        
        if (dto.getImageUrl() != null) {
            evenement.setImageUrl(dto.getImageUrl());
        }
        
        if (dto.getActif() != null) {
            evenement.setActif(dto.getActif());
        }
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     * @param evenements la liste d'entités
     * @return la liste de DTOs
     */
    public List<EvenementDTO> toDTOList(List<Evenement> evenements) {
        if (evenements == null) {
            return null;
        }

        return evenements.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de DTOs en liste d'entités
     * @param dtos la liste de DTOs
     * @return la liste d'entités
     */
    public List<Evenement> toEntityList(List<EvenementDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    /**
     * Crée un DTO simple avec seulement les informations de base
     * @param evenement l'entité
     * @return DTO simplifié
     */
    public EvenementDTO toSimpleDTO(Evenement evenement) {
        if (evenement == null) {
            return null;
        }

        EvenementDTO dto = new EvenementDTO();
        dto.setId(evenement.getId());
        dto.setTitre(evenement.getTitre());
        dto.setDateDebut(evenement.getDateDebut());
        dto.setDateFin(evenement.getDateFin());
        dto.setLieu(evenement.getLieu());
        dto.setPrixPlace(evenement.getPrixPlace());
        dto.setCategorie(evenement.getCategorie());

        return dto;
    }
}
