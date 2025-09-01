package com.eventconnect.controllers;

import com.eventconnect.config.JwtTokenProvider;
import com.eventconnect.dto.JwtResponse;
import com.eventconnect.dto.LoginRequest;
import com.eventconnect.dto.RegisterRequest;
import com.eventconnect.dto.UtilisateurDTO;
import com.eventconnect.entities.Role;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.repositories.RoleRepository;
import com.eventconnect.services.UtilisateurService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Contrôleur pour la gestion de l'authentification
 * Gère la connexion, l'inscription et la validation des tokens JWT
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;

    public AuthController(AuthenticationManager authenticationManager,
                         UtilisateurService utilisateurService,
                         PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider,
                         RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleRepository = roleRepository;
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getMotDePasse()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            Utilisateur utilisateur = utilisateurService.findByEmail(loginRequest.getEmail());
            
            return ResponseEntity.ok(new JwtResponse(
                jwt,
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de l'authentification", e);
            return ResponseEntity.badRequest()
                .body("Erreur: Email ou mot de passe incorrect!");
        }
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body("Erreur: Cet email est déjà utilisé!");
            }

            // Créer le nouvel utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(registerRequest.getNom());
            utilisateur.setPrenom(registerRequest.getPrenom());
            utilisateur.setEmail(registerRequest.getEmail());
            utilisateur.setMotDePasse(passwordEncoder.encode(registerRequest.getMotDePasse()));
            utilisateur.setTelephone(registerRequest.getTelephone());

            // Assigner le rôle PARTICIPANT par défaut
            Role participantRole = roleRepository.findByNom("PARTICIPANT")
                .orElseThrow(() -> new RuntimeException("Erreur: Rôle PARTICIPANT non trouvé"));
            utilisateur.setRoles(Arrays.asList(participantRole));

            Utilisateur savedUser = utilisateurService.creer(utilisateur);

            // Générer le token JWT
            String jwt = jwtTokenProvider.generateTokenFromUsername(savedUser.getEmail());

            return ResponseEntity.ok(new JwtResponse(
                jwt,
                savedUser.getEmail(),
                savedUser.getNom(),
                savedUser.getPrenom()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription", e);
            return ResponseEntity.badRequest()
                .body("Erreur: Impossible de créer le compte utilisateur!");
        }
    }

    /**
     * Obtenir les informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<UtilisateurDTO> getCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            Utilisateur utilisateur = utilisateurService.findByEmail(email);
            
            UtilisateurDTO utilisateurDTO = new UtilisateurDTO();
            utilisateurDTO.setId(utilisateur.getId());
            utilisateurDTO.setNom(utilisateur.getNom());
            utilisateurDTO.setPrenom(utilisateur.getPrenom());
            utilisateurDTO.setEmail(utilisateur.getEmail());

            return ResponseEntity.ok(utilisateurDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint de test pour vérifier l'authentification
     */
    @GetMapping("/test")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Authentification réussie!");
    }
}
