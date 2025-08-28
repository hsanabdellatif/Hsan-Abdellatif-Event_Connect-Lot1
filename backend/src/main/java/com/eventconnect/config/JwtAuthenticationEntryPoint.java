package com.eventconnect.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Point d'entrée pour les erreurs d'authentification JWT
 * Retourne une erreur 401 Unauthorized quand l'authentification échoue
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                          "Erreur: Accès non autorisé - Token JWT invalide ou manquant");
    }
}
