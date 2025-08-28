package com.eventconnect.utils;

import com.eventconnect.exceptions.BusinessValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Classe utilitaire pour les validations métier
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Component
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(?:\\+33|0)[1-9](?:[0-9]{8})$");

    /**
     * Valide un email
     * @param email l'email à valider
     * @throws BusinessValidationException si l'email n'est pas valide
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessValidationException("L'email ne peut pas être vide");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessValidationException("Le format de l'email n'est pas valide");
        }
    }

    /**
     * Valide un numéro de téléphone français
     * @param telephone le numéro à valider
     * @throws BusinessValidationException si le numéro n'est pas valide
     */
    public void validatePhoneNumber(String telephone) {
        if (telephone != null && !telephone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(telephone).matches()) {
                throw new BusinessValidationException("Le format du numéro de téléphone n'est pas valide");
            }
        }
    }

    /**
     * Valide les dates d'un événement
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @throws BusinessValidationException si les dates ne sont pas cohérentes
     */
    public void validateEventDates(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (dateDebut == null) {
            throw new BusinessValidationException("La date de début est obligatoire");
        }
        
        if (dateFin == null) {
            throw new BusinessValidationException("La date de fin est obligatoire");
        }
        
        if (dateDebut.isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException("La date de début ne peut pas être dans le passé");
        }
        
        if (dateFin.isBefore(dateDebut)) {
            throw new BusinessValidationException("La date de fin ne peut pas être antérieure à la date de début");
        }
        
        if (dateFin.isEqual(dateDebut)) {
            throw new BusinessValidationException("La date de fin doit être postérieure à la date de début");
        }
    }

    /**
     * Valide la capacité d'un événement
     * @param capaciteMax capacité maximale
     * @throws BusinessValidationException si la capacité n'est pas valide
     */
    public void validateEventCapacity(Integer capaciteMax) {
        if (capaciteMax == null) {
            throw new BusinessValidationException("La capacité maximale est obligatoire");
        }
        
        if (capaciteMax <= 0) {
            throw new BusinessValidationException("La capacité maximale doit être positive");
        }
        
        if (capaciteMax > 10000) {
            throw new BusinessValidationException("La capacité maximale ne peut pas dépasser 10 000 places");
        }
    }

    /**
     * Valide le prix d'un événement
     * @param prix le prix à valider
     * @throws BusinessValidationException si le prix n'est pas valide
     */
    public void validateEventPrice(java.math.BigDecimal prix) {
        if (prix == null) {
            throw new BusinessValidationException("Le prix est obligatoire");
        }
        
        if (prix.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Le prix ne peut pas être négatif");
        }
        
        if (prix.compareTo(new java.math.BigDecimal("99999.99")) > 0) {
            throw new BusinessValidationException("Le prix ne peut pas dépasser 99 999,99 €");
        }
    }

    /**
     * Valide le nombre de places pour une réservation
     * @param nombrePlaces nombre de places
     * @param placesDisponibles places disponibles
     * @throws BusinessValidationException si le nombre n'est pas valide
     */
    public void validateReservationPlaces(Integer nombrePlaces, Integer placesDisponibles) {
        if (nombrePlaces == null) {
            throw new BusinessValidationException("Le nombre de places est obligatoire");
        }
        
        if (nombrePlaces <= 0) {
            throw new BusinessValidationException("Le nombre de places doit être positif");
        }
        
        if (nombrePlaces > 50) {
            throw new BusinessValidationException("Impossible de réserver plus de 50 places en une fois");
        }
        
        if (placesDisponibles != null && nombrePlaces > placesDisponibles) {
            throw new BusinessValidationException(
                String.format("Seulement %d places sont disponibles", placesDisponibles)
            );
        }
    }

    /**
     * Valide qu'une chaîne n'est pas vide
     * @param value la valeur à valider
     * @param fieldName le nom du champ
     * @throws BusinessValidationException si la valeur est vide
     */
    public void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessValidationException(String.format("Le champ '%s' ne peut pas être vide", fieldName));
        }
    }

    /**
     * Valide la longueur d'une chaîne
     * @param value la valeur à valider
     * @param fieldName le nom du champ
     * @param minLength longueur minimale
     * @param maxLength longueur maximale
     * @throws BusinessValidationException si la longueur n'est pas valide
     */
    public void validateStringLength(String value, String fieldName, int minLength, int maxLength) {
        if (value != null) {
            if (value.length() < minLength) {
                throw new BusinessValidationException(
                    String.format("Le champ '%s' doit contenir au moins %d caractères", fieldName, minLength)
                );
            }
            
            if (value.length() > maxLength) {
                throw new BusinessValidationException(
                    String.format("Le champ '%s' ne peut pas dépasser %d caractères", fieldName, maxLength)
                );
            }
        }
    }

    /**
     * Valide qu'un ID est positif
     * @param id l'ID à valider
     * @param fieldName le nom du champ
     * @throws BusinessValidationException si l'ID n'est pas valide
     */
    public void validatePositiveId(Long id, String fieldName) {
        if (id == null) {
            throw new BusinessValidationException(String.format("L'ID %s est obligatoire", fieldName));
        }
        
        if (id <= 0) {
            throw new BusinessValidationException(String.format("L'ID %s doit être positif", fieldName));
        }
    }
}
