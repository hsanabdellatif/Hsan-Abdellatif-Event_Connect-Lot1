package com.eventconnect.services;

import com.eventconnect.entities.Role;
import com.eventconnect.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Service d'initialisation des données de base au démarrage de l'application
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Component
public class DataInitializationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializationService.class);

    private final RoleRepository roleRepository;

    public DataInitializationService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    /**
     * Initialise les rôles par défaut
     */
    private void initializeRoles() {
        try {
            // Créer les rôles s'ils n'existent pas
            for (Role.RoleName roleName : Role.RoleName.values()) {
                if (!roleRepository.existsByNom(roleName.getNom())) {
                    Role role = new Role(roleName.getNom(), roleName.getDescription());
                    roleRepository.save(role);
                    log.info("Rôle créé: {}", roleName.getNom());
                }
            }
            log.info("Initialisation des rôles terminée");
        } catch (Exception e) {
            log.error("Erreur lors de l'initialisation des rôles", e);
        }
    }
}
