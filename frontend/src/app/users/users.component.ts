import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  users: any[] = [];

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
