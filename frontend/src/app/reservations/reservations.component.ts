import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.css']
})
export class ReservationsComponent implements OnInit {

  reservations: any[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  getStatusColor(statut: string): string {
    switch(statut) {
      case 'CONFIRMEE': return 'success';
      case 'EN_ATTENTE': return 'warning';
      case 'ANNULEE': return 'danger';
      default: return 'info';
    }
  }

  getStatusLabel(statut: string): string {
    switch(statut) {
      case 'CONFIRMEE': return 'Confirmée';
      case 'EN_ATTENTE': return 'En attente';
      case 'ANNULEE': return 'Annulée';
      default: return statut;
    }
  }

  confirmReservation(id: number): void {
    const reservation = this.reservations.find(r => r.id === id);
    if (reservation) {
      reservation.statut = 'CONFIRMEE';
    }
  }

  cancelReservation(id: number): void {
    const reservation = this.reservations.find(r => r.id === id);
    if (reservation) {
      reservation.statut = 'ANNULEE';
    }
  }

  viewReservation(id: number): void {
    console.log('Voir détails réservation:', id);
  }

  getReservationsStats() {
    return {
      total: this.reservations.length,
      confirmees: this.reservations.filter(r => r.statut === 'CONFIRMEE').length,
      enAttente: this.reservations.filter(r => r.statut === 'EN_ATTENTE').length,
      annulees: this.reservations.filter(r => r.statut === 'ANNULEE').length,
      revenuTotal: this.reservations
        .filter(r => r.statut === 'CONFIRMEE')
        .reduce((sum, r) => sum + r.prixTotal, 0)
    };
  }
}
