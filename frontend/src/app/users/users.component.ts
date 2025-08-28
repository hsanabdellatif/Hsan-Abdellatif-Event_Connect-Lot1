import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  users: any[] = [
    {
      id: 1,
      nom: 'Dupont',
      prenom: 'Jean',
      email: 'jean.dupont@email.com',
      telephone: '+33123456789',
      dateInscription: '2025-01-15T10:30:00',
      actif: true,
      nombreReservations: 3,
      totalDepense: 450.00,
      role: 'USER'
    },
    {
      id: 2,
      nom: 'Martin',
      prenom: 'Marie',
      email: 'marie.martin@email.com',
      telephone: '+33987654321',
      dateInscription: '2025-02-20T14:15:00',
      actif: true,
      nombreReservations: 1,
      totalDepense: 200.00,
      role: 'USER'
    },
    {
      id: 3,
      nom: 'Durand',
      prenom: 'Pierre',
      email: 'pierre.durand@email.com',
      telephone: '+33456789123',
      dateInscription: '2025-03-10T09:00:00',
      actif: true,
      nombreReservations: 5,
      totalDepense: 0.00,
      role: 'USER'
    },
    {
      id: 4,
      nom: 'Admin',
      prenom: 'Super',
      email: 'admin@eventconnect.com',
      telephone: '+33111222333',
      dateInscription: '2025-01-01T00:00:00',
      actif: true,
      nombreReservations: 0,
      totalDepense: 0.00,
      role: 'ADMIN'
    },
    {
      id: 5,
      nom: 'Leroy',
      prenom: 'Sophie',
      email: 'sophie.leroy@email.com',
      telephone: '+33222333444',
      dateInscription: '2025-08-01T16:45:00',
      actif: false,
      nombreReservations: 2,
      totalDepense: 300.00,
      role: 'USER'
    }
  ];

  constructor() { }

  ngOnInit(): void {
  }

  getRoleColor(role: string): string {
    switch(role) {
      case 'ADMIN': return 'danger';
      case 'MODERATOR': return 'warning';
      case 'USER': return 'info';
      default: return 'secondary';
    }
  }

  getRoleLabel(role: string): string {
    switch(role) {
      case 'ADMIN': return 'Administrateur';
      case 'MODERATOR': return 'Modérateur';
      case 'USER': return 'Utilisateur';
      default: return role;
    }
  }

  toggleUserStatus(id: number): void {
    const user = this.users.find(u => u.id === id);
    if (user) {
      user.actif = !user.actif;
    }
  }

  editUser(id: number): void {
    console.log('Éditer utilisateur:', id);
  }

  viewUserReservations(id: number): void {
    console.log('Voir réservations de l\'utilisateur:', id);
  }

  deleteUser(id: number): void {
    console.log('Supprimer utilisateur:', id);
  }

  getUsersStats() {
    return {
      total: this.users.length,
      actifs: this.users.filter(u => u.actif).length,
      inactifs: this.users.filter(u => !u.actif).length,
      admins: this.users.filter(u => u.role === 'ADMIN').length,
      revenuTotal: this.users.reduce((sum, u) => sum + u.totalDepense, 0)
    };
  }
}
