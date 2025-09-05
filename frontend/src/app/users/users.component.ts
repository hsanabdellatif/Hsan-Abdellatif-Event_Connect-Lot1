import { Component, OnInit } from '@angular/core';
import { UserService, UtilisateurDto } from '../services/user.service';
import { catchError, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  users: UtilisateurDto[] = [];
  filteredUsers: UtilisateurDto[] = [];
  stats: any = { total: 0, actifs: 0, inactifs: 0, admins: 0, revenuTotal: 0 };
  selectedRole: string = '';
  selectedStatus: string = '';
  searchTerm: string = '';

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadStats();
  }

  // Charger tous les utilisateurs
  loadUsers(): void {
    this.userService.getAllUsers().pipe(
      tap(users => {
        this.users = users;
        this.applyFilters();
      }),
      catchError(error => {
        console.error('Erreur lors du chargement des utilisateurs:', error);
        return throwError(error);
      })
    ).subscribe();
  }

  // Charger les statistiques
  loadStats(): void {
    this.userService.getUserStats().pipe(
      tap(stats => {
        this.stats = stats;
      }),
      catchError(error => {
        console.error('Erreur lors du chargement des statistiques:', error);
        return throwError(error);
      })
    ).subscribe();
  }

  // Appliquer les filtres et la recherche
  applyFilters(): void {
    let filtered = [...this.users];

    // Filtre par rôle
    if (this.selectedRole) {
      filtered = filtered.filter(user => user.role === this.selectedRole);
    }

    // Filtre par statut
    if (this.selectedStatus) {
      const isActive = this.selectedStatus === 'true';
      filtered = filtered.filter(user => user.actif === isActive);
    }

    // Recherche
    if (this.searchTerm) {
      this.userService.searchUsers(this.searchTerm).pipe(
        tap(users => {
          filtered = users;
          this.filteredUsers = filtered;
        }),
        catchError(error => {
          console.error('Erreur lors de la recherche:', error);
          return throwError(error);
        })
      ).subscribe();
    } else {
      this.filteredUsers = filtered;
    }
  }

  // Couleur des rôles
  getRoleColor(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'danger';
      case 'ORGANISATEUR':
        return 'warning';
      case 'USER':
        return 'info';
      default:
        return 'secondary';
    }
  }

  // Libellé des rôles
  getRoleLabel(role: string): string {
    switch (role) {
      case 'ADMIN':
        return 'Administrateur';
      case 'ORGANISATEUR':
        return 'Organisateur';
      case 'USER':
        return 'Utilisateur';
      default:
        return role;
    }
  }

  // Activer/Désactiver un utilisateur
  toggleUserStatus(id: number): void {
    this.userService.toggleUserStatus(id).pipe(
      tap(updatedUser => {
        const user = this.users.find(u => u.id === id);
        if (user) {
          user.actif = updatedUser.actif;
          this.applyFilters();
          this.loadStats(); // Mettre à jour les statistiques
        }
      }),
      catchError(error => {
        console.error('Erreur lors du changement de statut:', error);
        return throwError(error);
      })
    ).subscribe();
  }

  // Éditer un utilisateur
  editUser(id: number): void {
    this.router.navigate(['/users/edit', id]);
  }

  // Voir les réservations d'un utilisateur
  viewUserReservations(id: number): void {
    this.router.navigate(['/users', id, 'reservations']);
  }

  // Supprimer un utilisateur
  deleteUser(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet utilisateur ?')) {
      this.userService.deleteUser(id).pipe(
        tap(() => {
          this.users = this.users.filter(u => u.id !== id);
          this.applyFilters();
          this.loadStats(); // Mettre à jour les statistiques
        }),
        catchError(error => {
          console.error('Erreur lors de la suppression:', error);
          return throwError(error);
        })
      ).subscribe();
    }
  }

  // Gérer les changements de filtres
  onRoleChange(event: Event): void {
    this.selectedRole = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  onStatusChange(event: Event): void {
    this.selectedStatus = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  onSearchChange(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }
}