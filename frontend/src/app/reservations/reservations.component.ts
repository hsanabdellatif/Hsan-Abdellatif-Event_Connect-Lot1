import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../services/reservation.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.css']
})
export class ReservationsComponent implements OnInit {
  reservations: any[] = [];
  filteredReservations: any[] = [];
  loading = true;
  error = '';
  selectedStatus: string = '';
  searchQuery: string = '';

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.loading = true;
    this.error = '';
    this.reservationService.getAllReservations().pipe(
      catchError(err => {
        this.error = err.message || 'Erreur lors du chargement des réservations';
        this.loading = false;
        return of([]);
      })
    ).subscribe(reservations => {
      this.reservations = reservations;
      this.filteredReservations = reservations;
      this.loading = false;
    });
  }

  getStatusColor(statut: string): string {
    switch (statut) {
      case 'CONFIRMEE': return 'success';
      case 'EN_ATTENTE': return 'warning';
      case 'ANNULEE': return 'danger';
      default: return 'info';
    }
  }

  getStatusLabel(statut: string): string {
    switch (statut) {
      case 'CONFIRMEE': return 'Confirmée';
      case 'EN_ATTENTE': return 'En attente';
      case 'ANNULEE': return 'Annulée';
      default: return statut;
    }
  }

  confirmReservation(id: number): void {
    this.reservationService.confirmerReservation(id).pipe(
      catchError(err => {
        this.error = err.message || 'Erreur lors de la confirmation de la réservation';
        return of(null);
      })
    ).subscribe(updatedReservation => {
      if (updatedReservation) {
        const reservation = this.reservations.find(r => r.id === id);
        if (reservation) {
          reservation.statut = 'CONFIRMEE';
          this.applyFilters();
        }
      }
    });
  }

  cancelReservation(id: number): void {
    this.reservationService.annulerReservation(id).pipe(
      catchError(err => {
        this.error = err.message || 'Erreur lors de l\'annulation de la réservation';
        return of(null);
      })
    ).subscribe(updatedReservation => {
      if (updatedReservation) {
        const reservation = this.reservations.find(r => r.id === id);
        if (reservation) {
          reservation.statut = 'ANNULEE';
          this.applyFilters();
        }
      }
    });
  }

  viewReservation(id: number): void {
    console.log('Voir détails réservation:', id);
    // Implement navigation to a details page if needed
  }

  getReservationsStats() {
    return {
      total: this.filteredReservations.length,
      confirmees: this.filteredReservations.filter(r => r.statut === 'CONFIRMEE').length,
      enAttente: this.filteredReservations.filter(r => r.statut === 'EN_ATTENTE').length,
      annulees: this.filteredReservations.filter(r => r.statut === 'ANNULEE').length,
      revenuTotal: this.filteredReservations
        .filter(r => r.statut === 'CONFIRMEE')
        .reduce((sum, r) => sum + (r.montantTotal || 0), 0)
    };
  }

  filterByStatus(): void {
    this.applyFilters();
  }

  searchReservations(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    let filtered = [...this.reservations];

    // Apply status filter
    if (this.selectedStatus) {
      filtered = filtered.filter(r => r.statut === this.selectedStatus);
    }

    // Apply search query
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(r =>
        r.utilisateur.nomComplet.toLowerCase().includes(query) ||
        r.evenement.titre.toLowerCase().includes(query)
      );
    }

    this.filteredReservations = filtered;
  }

  retryLoading(): void {
    this.error = '';
    this.loadReservations();
  }
}