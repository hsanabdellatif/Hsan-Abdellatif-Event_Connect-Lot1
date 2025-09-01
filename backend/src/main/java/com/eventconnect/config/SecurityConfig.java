package com.eventconnect.config;

import com.eventconnect.services.UtilisateurService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration de sécurité Spring Security avec JWT
 * Configure l'authentification, l'autorisation et la gestion des tokens JWT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UtilisateurService utilisateurService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                         UtilisateurService utilisateurService,
                         JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.utilisateurService = utilisateurService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     * Configuration de la chaîne de filtres de sécurité
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics
                .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                // Documentation API (optionnel)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Endpoints pour les organisateurs
                .requestMatchers("/evenements").hasAnyRole("ADMIN", "ORGANISATEUR", "PARTICIPANT")
                .requestMatchers("/evenements/**").hasAnyRole("ADMIN", "ORGANISATEUR", "PARTICIPANT")
                // Gestion des événements - création, modification, suppression
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/evenements").hasAnyRole("ADMIN", "ORGANISATEUR")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/evenements/**").hasAnyRole("ADMIN", "ORGANISATEUR")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/evenements/**").hasAnyRole("ADMIN", "ORGANISATEUR")
                // Réservations
                .requestMatchers("/reservations/**").hasAnyRole("ADMIN", "ORGANISATEUR", "PARTICIPANT")
                // Administration des utilisateurs
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour permettre les requêtes cross-origin
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Origin"
        ));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean pour l'encodage des mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean pour le service de détails utilisateur
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return utilisateurService;
    }

    /**
     * Provider d'authentification personnalisé
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean pour le gestionnaire d'authentification
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
