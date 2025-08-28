import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.css']
})
export class ReservationsComponent implements OnInit {

  reservations: any[] = [
    {
      id: 1,
      utilisateurNom: 'Jean Dupont',
      utilisateurEmail: 'jean.dupont@email.com',
      evenementNom: 'Conférence Tech 2025',
      dateReservation: '2025-08-15T10:30:00',
      nombrePlaces: 2,
      prixTotal: 300.00,
      statut: 'CONFIRMEE'
    },
    {
      id: 2,
      utilisateurNom: 'Marie Martin',
      utilisateurEmail: 'marie.martin@email.com',
      evenementNom: 'Workshop Angular',
      dateReservation: '2025-08-20T14:15:00',
      nombrePlaces: 1,
      prixTotal: 200.00,
      statut: 'EN_ATTENTE'
    },
    {
      id: 3,
      utilisateurNom: 'Pierre Durand',
      utilisateurEmail: 'pierre.durand@email.com',
      evenementNom: 'Meetup DevOps',
      dateReservation: '2025-08-25T09:00:00',
      nombrePlaces: 1,
      prixTotal: 0.00,
      statut: 'CONFIRMEE'
    },
    {
      id: 4,
      utilisateurNom: 'Sophie Leroy',
      utilisateurEmail: 'sophie.leroy@email.com',
      evenementNom: 'Conférence Tech 2025',
      dateReservation: '2025-08-22T16:45:00',
      nombrePlaces: 1,
      prixTotal: 150.00,
      statut: 'ANNULEE'
    }
  ];

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
