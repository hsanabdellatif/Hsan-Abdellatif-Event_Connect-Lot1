package com.eventconnect.exceptions;

/**
 * Exception personnalisée pour les erreurs liées aux ressources non trouvées
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructeur avec message
     * @param message le message d'erreur
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     * @param message le message d'erreur
     * @param cause la cause de l'exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur pour une ressource spécifique
     * @param resourceName le nom de la ressource
     * @param fieldName le nom du champ
     * @param fieldValue la valeur du champ
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s non trouvé avec %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
