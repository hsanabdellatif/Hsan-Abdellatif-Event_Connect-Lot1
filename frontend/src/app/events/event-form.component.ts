import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../services/event.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.css']
})
export class EventFormComponent implements OnInit {
  event: any = {
    id: null, // Ajouter id pour éviter undefined
    titre: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    lieu: '',
    capaciteMax: 1,
    prix: 0,
    categorie: 'CONFERENCE',
    imageUrl: '',
    organisateur: { id: null }
  };
  isEditMode = false;
  error: string | null = null;
  categories = [
    'CONFERENCE', 'WORKSHOP', 'NETWORKING', 'FORMATION',
    'SEMINAIRE', 'CONCERT', 'SPORT', 'CULTUREL', 'AUTRE'
  ];
  organisateurs: any[] = [];

  constructor(
    private eventService: EventService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.eventService.getOrganisateurs().pipe(
      catchError(err => {
        this.error = 'Erreur lors du chargement des organisateurs : ' + err.message;
        return of([]);
      })
    ).subscribe(organisateurs => {
      this.organisateurs = organisateurs;
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.eventService.trouverParId(+id).pipe(
        catchError(err => {
          this.error = `Erreur lors du chargement de l'événement avec l'ID ${id} : ${err.message}`;
          return of(null);
        })
      ).subscribe(event => {
        if (event) {
          this.event = {
            id: event.id,
            titre: event.titre || '',
            description: event.description || '',
            dateDebut: new Date(event.dateDebut).toISOString().slice(0, 16),
            dateFin: new Date(event.dateFin).toISOString().slice(0, 16),
            lieu: event.lieu || '',
            capaciteMax: event.capaciteMax || 1,
            prix: Number(event.prix) || 0,
            categorie: event.categorie || 'CONFERENCE',
            imageUrl: event.imageUrl || '',
            organisateur: { id: event.organisateur?.id || null }
          };
        } else {
          this.router.navigate(['/events'], { queryParams: { error: 'Événement non trouvé' } });
        }
      });
    }
  }

  validateForm(): boolean {
    if (!this.event.titre || this.event.titre.length < 3 || this.event.titre.length > 100) {
      this.error = 'Le titre doit contenir entre 3 et 100 caractères';
      return false;
    }
    if (!this.event.lieu || this.event.lieu.length < 3 || this.event.lieu.length > 200) {
      this.error = 'Le lieu doit contenir entre 3 et 200 caractères';
      return false;
    }
    if (!this.event.dateDebut || !this.event.dateFin) {
      this.error = 'Les dates de début et de fin sont obligatoires';
      return false;
    }
    const dateDebut = new Date(this.event.dateDebut);
    const dateFin = new Date(this.event.dateFin);
    if (dateFin <= dateDebut) {
      this.error = 'La date de fin doit être postérieure à la date de début';
      return false;
    }
    if (dateDebut < new Date()) {
      this.error = 'La date de début doit être dans le futur';
      return false;
    }
    if (!this.event.capaciteMax || this.event.capaciteMax < 1 || this.event.capaciteMax > 10000) {
      this.error = 'La capacité maximale doit être entre 1 et 10000';
      return false;
    }
    if (this.event.prix == null || this.event.prix < 0) {
      this.error = 'Le prix ne peut pas être négatif';
      return false;
    }
    if (!this.event.categorie || !this.categories.includes(this.event.categorie)) {
      this.error = 'Catégorie invalide';
      return false;
    }
    if (!this.event.organisateur?.id) {
      this.error = 'Un organisateur doit être sélectionné';
      return false;
    }
    if (this.isEditMode && !this.event.id) {
      this.error = 'ID de l\'événement manquant pour la mise à jour';
      return false;
    }
    return true;
  }

  saveEvent(): void {
    this.error = null;
    if (!this.validateForm()) {
      return;
    }

    const eventData = {
      titre: this.event.titre,
      description: this.event.description,
      dateDebut: new Date(this.event.dateDebut).toISOString(),
      dateFin: new Date(this.event.dateFin).toISOString(),
      lieu: this.event.lieu,
      placesMax: this.event.capaciteMax,
      prix: Number(this.event.prix),
      categorie: this.event.categorie,
      imageUrl: this.event.imageUrl,
      organisateur: { id: Number(this.event.organisateur.id) }
    };

    console.log('Données envoyées au backend:', eventData);

    const action = this.isEditMode
      ? this.eventService.updateEvent(this.event.id, eventData)
      : this.eventService.createEvent(eventData);

    action.pipe(
      catchError(err => {
        this.error = 'Erreur lors de la sauvegarde de l\'événement : ' + err.message;
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        this.router.navigate(['/events']);
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/events']);
  }
}