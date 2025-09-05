import { Component, OnInit } from '@angular/core';
import { EventService } from '../services/event.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  events: any[] = [];
  loading = true;
  error: string | null = null;
  searchTerm = '';

  constructor(private eventService: EventService, private router: Router) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading = true;
    this.error = null;
    const observable = this.searchTerm
      ? this.eventService.searchEvents(this.searchTerm)
      : this.eventService.getActiveEvents();

    observable.pipe(
      catchError(err => {
        this.error = 'Erreur lors du chargement des événements : ' + err.message;
        this.loading = false;
        return of([]);
      })
    ).subscribe(events => {
      this.events = events;
      this.loading = false;
    });
  }

  searchEvents(term: string): void {
    this.searchTerm = term;
    this.loadEvents();
  }

  getStatusColor(statut: string): string {
    switch (statut) {
      case 'PLANIFIE': return 'success';
      case 'EN_COURS': return 'info';
      case 'TERMINE': return 'secondary';
      case 'ANNULE': return 'danger';
      case 'REPORTE': return 'warning';
      default: return 'info';
    }
  }

  getPlacesDisponibles(event: any): number {
    return event.placesDisponibles || 0;
  }

  createEvent(): void {
    this.router.navigate(['/events/create']);
  }

  editEvent(id: number): void {
    this.router.navigate(['/events/edit', id]);
  }

  deleteEvent(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet événement ?')) {
      this.eventService.deleteEvent(id).pipe(
        catchError(err => {
          this.error = 'Erreur lors de la suppression de l\'événement : ' + err.message;
          return of(null);
        })
      ).subscribe(() => {
        this.events = this.events.filter(event => event.id !== id);
      });
    }
  }

  viewReservations(id: number): void {
    this.router.navigate(['/events/reservations', id]);
  }

  getTotalReservations(): number {
    return this.events.reduce((sum, e) => sum + (e.placesReservees || 0), 0);
  }

  getTotalPlacesDisponibles(): number {
    return this.events.reduce((sum, e) => sum + (e.placesDisponibles || 0), 0);
  }

  getTotalRevenues(): number {
    return this.events.reduce((sum, e) => sum + (e.chiffreAffaires || 0), 0);
  }
}