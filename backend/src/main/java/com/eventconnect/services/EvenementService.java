package com.eventconnect.services;

import com.eventconnect.entities.Evenement;
import com.eventconnect.repositories.EvenementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des événements
 *
 * @author EventConnect Team
 * @version 2.0.0
 */
@Service
@Transactional
public class EvenementService {

    private static final Logger log = LoggerFactory.getLogger(EvenementService.class);
    private final EvenementRepository evenementRepository;

    public EvenementService(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    public Evenement creerEvenement(Evenement evenement) {
        log.info("Création d'un nouvel événement: {}", evenement.getTitre());

        if (evenement.getDateFin().isBefore(evenement.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        if (evenement.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }

        try {
            Evenement.CategorieEvenement.valueOf(evenement.getCategorie().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Catégorie invalide: " + evenement.getCategorie());
        }

        Evenement nouvelEvenement = evenementRepository.save(evenement);
        log.info("Événement créé avec succès, ID: {}", nouvelEvenement.getId());
        return nouvelEvenement;
    }

    public Evenement mettreAJourEvenement(Long id, Evenement evenementMiseAJour) {
        log.info("Mise à jour de l'événement ID: {}", id);

        Evenement evenementExistant = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + id));

        try {
            Evenement.CategorieEvenement.valueOf(evenementMiseAJour.getCategorie().name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Catégorie invalide: " + evenementMiseAJour.getCategorie());
        }

        evenementExistant.setTitre(evenementMiseAJour.getTitre());
        evenementExistant.setDescription(evenementMiseAJour.getDescription());
        evenementExistant.setDateDebut(evenementMiseAJour.getDateDebut());
        evenementExistant.setDateFin(evenementMiseAJour.getDateFin());
        evenementExistant.setLieu(evenementMiseAJour.getLieu());
        evenementExistant.setPlacesMax(evenementMiseAJour.getPlacesMax());
        evenementExistant.setPrix(evenementMiseAJour.getPrix());
        evenementExistant.setCategorie(evenementMiseAJour.getCategorie());

        if (evenementExistant.getDateFin().isBefore(evenementExistant.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        Evenement evenementMisAJour = evenementRepository.save(evenementExistant);
        log.info("Événement mis à jour avec succès, ID: {}", evenementMisAJour.getId());
        return evenementMisAJour;
    }

    @Transactional(readOnly = true)
    public Optional<Evenement> trouverParId(Long id) {
        log.info("Recherche de l'événement ID: {}", id);
        return evenementRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Evenement> obtenirTousLesEvenements(Pageable pageable) {
        log.info("Récupération de tous les événements avec pagination");
        return evenementRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Evenement> rechercherParTitre(String titre) {
        log.info("Recherche d'événements par titre: {}", titre);
        return evenementRepository.findByTitreContaining(titre);
    }

    @Transactional(readOnly = true)
    public List<Evenement> obtenirEvenementsFuturs() {
        log.info("Récupération des événements futurs");
        return evenementRepository.findByDateDebutGreaterThan(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Evenement> obtenirEvenementsParCategorie(String categorie) {
        log.info("Récupération des événements de la catégorie: {}", categorie);
        try {
            Evenement.CategorieEvenement cat = Evenement.CategorieEvenement.valueOf(categorie);
            return evenementRepository.findByCategorie(cat);
        } catch (IllegalArgumentException e) {
            log.warn("Catégorie invalide: {}", categorie);
            throw new IllegalArgumentException("Catégorie invalide: " + categorie);
        }
    }

    @Transactional(readOnly = true)
    public List<Evenement> obtenirEvenementsDisponibles() {
        log.info("Récupération des événements disponibles");
        return evenementRepository.findEvenementsAvecPlacesDisponibles();
    }

    public void supprimerEvenement(Long id) {
        log.info("Suppression de l'événement ID: {}", id);

        if (!evenementRepository.existsById(id)) {
            throw new RuntimeException("Événement non trouvé avec l'ID: " + id);
        }

        evenementRepository.deleteById(id);
        log.info("Événement supprimé avec succès, ID: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean verifierDisponibilite(Long id, Integer nombrePlaces) {
        log.info("Vérification de disponibilité pour l'événement ID: {}, places demandées: {}", id, nombrePlaces);

        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec l'ID: " + id));

        Integer placesReservees = evenementRepository.countPlacesReserveesParEvenement(id);
        Integer placesDisponibles = evenement.getPlacesMax() - placesReservees;

        return placesDisponibles >= nombrePlaces;
    }

    @Transactional(readOnly = true)
    public long compterEvenements() {
        return evenementRepository.count();
    }

    @Transactional(readOnly = true)
    public List<Evenement> getEvenementsActifs() {
        log.info("Récupération des événements actifs");
        return evenementRepository.findAll().stream()
                .filter(e -> e.getActif() &&
                        e.getStatut() == Evenement.StatutEvenement.PLANIFIE &&
                        e.getDateDebut().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}