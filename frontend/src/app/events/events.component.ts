import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  events: any[] = [
    {
      id: 1,
      nom: 'Conférence Tech 2025',
      description: 'Grande conférence sur les nouvelles technologies',
      dateDebut: '2025-09-15',
      dateFin: '2025-09-16',
      lieu: 'Centre de Congrès, Paris',
      nombrePlaces: 500,
      placesReservees: 234,
      prix: 150.00,
      statut: 'ACTIF'
    },
    {
      id: 2,
      nom: 'Workshop Angular',
      description: 'Formation pratique sur Angular 17',
      dateDebut: '2025-09-20',
      dateFin: '2025-09-20',
      lieu: 'École de Développement, Lyon',
      nombrePlaces: 30,
      placesReservees: 28,
      prix: 200.00,
      statut: 'COMPLET'
    },
    {
      id: 3,
      nom: 'Meetup DevOps',
      description: 'Rencontre des professionnels DevOps',
      dateDebut: '2025-10-05',
      dateFin: '2025-10-05',
      lieu: 'Hub Numérique, Toulouse',
      nombrePlaces: 80,
      placesReservees: 45,
      prix: 0.00,
      statut: 'ACTIF'
    }
  ];

  constructor() { }

  ngOnInit(): void {
  }

  getStatusColor(statut: string): string {
    switch(statut) {
      case 'ACTIF': return 'success';
      case 'COMPLET': return 'warning';
      case 'ANNULE': return 'danger';
      default: return 'info';
    }
  }

  getPlacesDisponibles(event: any): number {
    return event.nombrePlaces - event.placesReservees;
  }

  editEvent(id: number): void {
    console.log('Éditer événement:', id);
  }

  deleteEvent(id: number): void {
    console.log('Supprimer événement:', id);
  }

  viewReservations(id: number): void {
    console.log('Voir réservations pour événement:', id);
  }
}
