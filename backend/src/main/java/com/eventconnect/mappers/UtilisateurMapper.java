package com.eventconnect.mappers;

import com.eventconnect.dto.UtilisateurDTO;
import com.eventconnect.entities.Utilisateur;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre Utilisateur et UtilisateurDTO
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@Component
public class UtilisateurMapper {

    /**
     * Convertit une entité Utilisateur en DTO
     * @param utilisateur l'entité à convertir
     * @return le DTO correspondant
     */
    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        return new UtilisateurDTO(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getDateCreation(),
                utilisateur.getDateModification(),
                utilisateur.getActif()
        );
    }

    /**
     * Convertit un DTO en entité Utilisateur
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    public Utilisateur toEntity(UtilisateurDTO dto) {
        if (dto == null) {
            return null;
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(dto.getMotDePasse());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setDateCreation(dto.getDateInscription());
        utilisateur.setDateModification(dto.getDateModification());
        utilisateur.setActif(dto.getActif());

        return utilisateur;
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     * @param utilisateur l'entité à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     */
    public void updateEntityFromDTO(Utilisateur utilisateur, UtilisateurDTO dto) {
        if (utilisateur == null || dto == null) {
            return;
        }

        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setTelephone(dto.getTelephone());

        // Le mot de passe n'est mis à jour que s'il est fourni
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().trim().isEmpty()) {
            utilisateur.setMotDePasse(dto.getMotDePasse());
        }

        if (dto.getActif() != null) {
            utilisateur.setActif(dto.getActif());
        }
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     * @param utilisateurs la liste d'entités
     * @return la liste de DTOs
     */
    public List<UtilisateurDTO> toDTOList(List<Utilisateur> utilisateurs) {
        if (utilisateurs == null) {
            return null;
        }

        return utilisateurs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de DTOs en liste d'entités
     * @param dtos la liste de DTOs
     * @return la liste d'entités
     */
    public List<Utilisateur> toEntityList(List<UtilisateurDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Crée un DTO simple avec seulement les informations de base
     * @param utilisateur l'entité
     * @return DTO simplifié
     */
    public UtilisateurDTO toSimpleDTO(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());

        return dto;
    }
}