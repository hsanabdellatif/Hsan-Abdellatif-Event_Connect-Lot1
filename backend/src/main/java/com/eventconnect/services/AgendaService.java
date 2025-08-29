package com.eventconnect.services;

import com.eventconnect.entities.Evenement;
import com.eventconnect.entities.Utilisateur;
import com.eventconnect.repositories.EvenementRepository;
import com.eventconnect.repositories.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion des conflits d'agenda et la proposition de créneaux
 * 
 * @author EventConnect Team
 * @version 2.0.0
 */
@Service
@Transactional
public class AgendaService {

    private static final Logger log = LoggerFactory.getLogger(AgendaService.class);

    private final EvenementRepository evenementRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AgendaService(EvenementRepository evenementRepository, UtilisateurRepository utilisateurRepository) {
        this.evenementRepository = evenementRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Détecte les conflits d'agenda pour un organisateur
     * @param organisateurId l'ID de l'organisateur
     * @param dateDebut date de début proposée
     * @param dateFin date de fin proposée
     * @return true s'il y a un conflit
     */
    @Transactional(readOnly = true)
    public boolean detecterConflitAgenda(Long organisateurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Détection de conflit d'agenda pour l'organisateur {} entre {} et {}", 
                organisateurId, dateDebut, dateFin);

        Utilisateur organisateur = utilisateurRepository.findById(organisateurId)
            .orElseThrow(() -> new RuntimeException("Organisateur non trouvé"));

        List<Evenement> evenementsExistants = evenementRepository
            .findEvenementsConflitPourOrganisateur(organisateur, dateDebut, dateFin);

        boolean conflit = !evenementsExistants.isEmpty();
        
        if (conflit) {
            log.warn("Conflit détecté avec {} événement(s) existant(s)", evenementsExistants.size());
            for (Evenement event : evenementsExistants) {
                log.warn("Conflit avec l'événement '{}' du {} au {}", 
                        event.getTitre(), event.getDateDebut(), event.getDateFin());
            }
        }

        return conflit;
    }

    /**
     * Obtient la liste des événements en conflit
     * @param organisateurId l'ID de l'organisateur
     * @param dateDebut date de début proposée
     * @param dateFin date de fin proposée
     * @return la liste des événements en conflit
     */
    @Transactional(readOnly = true)
    public List<Evenement> obtenirEvenementsEnConflit(Long organisateurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Récupération des événements en conflit pour l'organisateur {}", organisateurId);

        Utilisateur organisateur = utilisateurRepository.findById(organisateurId)
            .orElseThrow(() -> new RuntimeException("Organisateur non trouvé"));

        return evenementRepository.findEvenementsConflitPourOrganisateur(organisateur, dateDebut, dateFin);
    }

    /**
     * Propose des créneaux libres pour un organisateur
     * @param organisateurId l'ID de l'organisateur
     * @param dateDebutSouhaitee date de début souhaitée
     * @param dureeHeures durée en heures de l'événement
     * @param nombreJoursRecherche nombre de jours à analyser
     * @return liste des créneaux libres proposés
     */
    @Transactional(readOnly = true)
    public List<CreneauLibre> proposerCreneauxLibres(Long organisateurId, LocalDateTime dateDebutSouhaitee, 
                                                    int dureeHeures, int nombreJoursRecherche) {
        log.info("Proposition de créneaux libres pour l'organisateur {} à partir du {} pour {} heures", 
                organisateurId, dateDebutSouhaitee, dureeHeures);

        Utilisateur organisateur = utilisateurRepository.findById(organisateurId)
            .orElseThrow(() -> new RuntimeException("Organisateur non trouvé"));

        List<CreneauLibre> creneauxLibres = new ArrayList<>();
        LocalDateTime dateRecherche = dateDebutSouhaitee.toLocalDate().atStartOfDay();
        LocalDateTime finRecherche = dateRecherche.plusDays(nombreJoursRecherche);

        // Récupération de tous les événements de l'organisateur dans la période
        List<Evenement> evenementsExistants = evenementRepository
            .findByOrganisateurAndDateDebutBetweenOrderByDateDebut(
                organisateur, dateRecherche, finRecherche);

        // Analyse jour par jour
        for (int jour = 0; jour < nombreJoursRecherche; jour++) {
            LocalDateTime debutJour = dateRecherche.plusDays(jour);
            List<CreneauLibre> creneauxDuJour = analyserCreneauxJour(
                debutJour, dureeHeures, evenementsExistants);
            creneauxLibres.addAll(creneauxDuJour);
        }

        log.info("{} créneaux libres trouvés", creneauxLibres.size());
        return creneauxLibres;
    }

    /**
     * Analyse les créneaux libres pour une journée donnée
     * @param debutJour début de la journée à analyser
     * @param dureeHeures durée requise en heures
     * @param evenementsExistants événements existants
     * @return liste des créneaux libres pour cette journée
     */
    private List<CreneauLibre> analyserCreneauxJour(LocalDateTime debutJour, int dureeHeures, 
                                                   List<Evenement> evenementsExistants) {
        List<CreneauLibre> creneauxJour = new ArrayList<>();
        
        // Heures d'ouverture : 8h00 à 22h00
        LocalDateTime debutPeriode = debutJour.with(LocalTime.of(8, 0));
        LocalDateTime finPeriode = debutJour.with(LocalTime.of(22, 0));

        // Événements de cette journée
        List<Evenement> evenementsJour = evenementsExistants.stream()
            .filter(e -> e.getDateDebut().toLocalDate().equals(debutJour.toLocalDate()) ||
                        e.getDateFin().toLocalDate().equals(debutJour.toLocalDate()) ||
                        (e.getDateDebut().isBefore(debutJour) && e.getDateFin().isAfter(debutJour.plusDays(1))))
            .sorted((e1, e2) -> e1.getDateDebut().compareTo(e2.getDateDebut()))
            .toList();

        LocalDateTime curseur = debutPeriode;

        for (Evenement evenement : evenementsJour) {
            LocalDateTime debutEvent = evenement.getDateDebut().isBefore(debutPeriode) ? 
                debutPeriode : evenement.getDateDebut();
            LocalDateTime finEvent = evenement.getDateFin().isAfter(finPeriode) ? 
                finPeriode : evenement.getDateFin();

            // Vérifier s'il y a un créneau libre avant cet événement
            if (curseur.plusHours(dureeHeures).isBefore(debutEvent) || 
                curseur.plusHours(dureeHeures).equals(debutEvent)) {
                
                CreneauLibre creneau = new CreneauLibre(
                    curseur, 
                    curseur.plusHours(dureeHeures),
                    "Créneau libre de " + dureeHeures + "h"
                );
                creneauxJour.add(creneau);
            }

            curseur = finEvent;
        }

        // Vérifier s'il reste un créneau en fin de journée
        if (curseur.plusHours(dureeHeures).isBefore(finPeriode) || 
            curseur.plusHours(dureeHeures).equals(finPeriode)) {
            
            CreneauLibre creneau = new CreneauLibre(
                curseur, 
                curseur.plusHours(dureeHeures),
                "Créneau libre de " + dureeHeures + "h en fin de journée"
            );
            creneauxJour.add(creneau);
        }

        return creneauxJour;
    }

    /**
     * Vérifie si une période est entièrement libre pour un organisateur
     * @param organisateurId l'ID de l'organisateur
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return true si la période est libre
     */
    @Transactional(readOnly = true)
    public boolean estPeriodeLibre(Long organisateurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        return !detecterConflitAgenda(organisateurId, dateDebut, dateFin);
    }

    /**
     * Propose des alternatives pour résoudre un conflit
     * @param organisateurId l'ID de l'organisateur
     * @param dateDebutSouhaitee date de début souhaitée
     * @param dureeHeures durée en heures
     * @return liste d'alternatives proposées
     */
    @Transactional(readOnly = true)
    public List<CreneauLibre> proposerAlternatives(Long organisateurId, LocalDateTime dateDebutSouhaitee, int dureeHeures) {
        log.info("Proposition d'alternatives pour l'organisateur {} à partir du {}", 
                organisateurId, dateDebutSouhaitee);

        // Proposer des créneaux sur 7 jours
        List<CreneauLibre> alternatives = proposerCreneauxLibres(
            organisateurId, dateDebutSouhaitee, dureeHeures, 7);

        // Trier par proximité avec la date souhaitée
        alternatives.sort((c1, c2) -> {
            long diff1 = Math.abs(java.time.Duration.between(dateDebutSouhaitee, c1.getDebut()).toHours());
            long diff2 = Math.abs(java.time.Duration.between(dateDebutSouhaitee, c2.getDebut()).toHours());
            return Long.compare(diff1, diff2);
        });

        // Retourner les 5 meilleures alternatives
        return alternatives.stream().limit(5).toList();
    }

    /**
     * Classe interne représentant un créneau libre
     */
    public static class CreneauLibre {
        private LocalDateTime debut;
        private LocalDateTime fin;
        private String description;

        public CreneauLibre(LocalDateTime debut, LocalDateTime fin, String description) {
            this.debut = debut;
            this.fin = fin;
            this.description = description;
        }

        // Getters et setters
        public LocalDateTime getDebut() { return debut; }
        public void setDebut(LocalDateTime debut) { this.debut = debut; }

        public LocalDateTime getFin() { return fin; }
        public void setFin(LocalDateTime fin) { this.fin = fin; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @Override
        public String toString() {
            return "CreneauLibre{" +
                    "debut=" + debut +
                    ", fin=" + fin +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
