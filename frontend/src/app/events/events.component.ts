import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  events: any[] = [];

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

  // Méthodes pour les statistiques
  getTotalReservations(): number {
    return this.events.reduce((sum, e) => sum + e.placesReservees, 0);
  }

  getTotalPlacesDisponibles(): number {
    return this.events.reduce((sum, e) => sum + (e.nombrePlaces - e.placesReservees), 0);
  }

  getTotalRevenues(): number {
    return this.events.reduce((sum, e) => sum + (e.prix * e.placesReservees), 0);
  }
}
