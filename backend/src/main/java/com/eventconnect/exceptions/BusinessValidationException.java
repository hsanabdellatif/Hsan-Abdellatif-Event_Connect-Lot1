package com.eventconnect.exceptions;

/**
 * Exception personnalisée pour les erreurs de validation métier
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
public class BusinessValidationException extends RuntimeException {

    /**
     * Constructeur avec message
     * @param message le message d'erreur
     */
    public BusinessValidationException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     * @param message le message d'erreur
     * @param cause la cause de l'exception
     */
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
