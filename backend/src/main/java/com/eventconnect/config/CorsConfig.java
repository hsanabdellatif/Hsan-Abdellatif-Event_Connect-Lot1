package com.eventconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration CORS pour l'application EventConnect
 * Permet la communication entre le frontend et le backend
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // Configuration CORS déplacée vers SecurityConfig
}
