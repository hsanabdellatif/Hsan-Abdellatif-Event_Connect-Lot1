import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UtilisateurDto {
id?: number;
  nom: string;
  prenom: string;
  email: string;
  motDePasse?: string; // Optionnel, utilisé uniquement pour création/modification
  telephone: string;
  dateInscription?: string; // LocalDateTime sera converti en string ISO
  dateModification?: string;
  actif: boolean;
  role: string; // Correspond à RoleUtilisateur (USER, ADMIN, ORGANISATEUR)
  pointsFidelite?: number;
  niveauFidelite?: string; // BRONZE, ARGENT, OR, DIAMANT
  totalReservations?: number;
  totalEvenementsOrganises?: number;
  totalDepense?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/utilisateurs`;

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // Récupérer tous les utilisateurs
  getAllUsers(): Observable<UtilisateurDto[]> {
    return this.http.get<UtilisateurDto[]>(this.apiUrl);
  }

  // Récupérer un utilisateur par ID
  getUserById(id: number): Observable<UtilisateurDto> {
    return this.http.get<UtilisateurDto>(`${this.apiUrl}/${id}`);
  }

  // Créer un nouvel utilisateur
  createUser(user: UtilisateurDto): Observable<UtilisateurDto> {
    return this.http.post<UtilisateurDto>(this.apiUrl, user, this.httpOptions);
  }

  // Mettre à jour un utilisateur
  updateUser(id: number, user: UtilisateurDto): Observable<UtilisateurDto> {
    return this.http.put<UtilisateurDto>(`${this.apiUrl}/${id}`, user, this.httpOptions);
  }

  // Supprimer un utilisateur
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Activer/Désactiver un utilisateur
  toggleUserStatus(id: number): Observable<UtilisateurDto> {
    return this.http.patch<UtilisateurDto>(`${this.apiUrl}/${id}/toggle-status`, {}, this.httpOptions);
  }

  // Rechercher des utilisateurs
  searchUsers(searchTerm: string): Observable<UtilisateurDto[]> {
    return this.http.get<UtilisateurDto[]>(`${this.apiUrl}/search?q=${searchTerm}`);
  }

  // Récupérer les utilisateurs par email
  getUserByEmail(email: string): Observable<UtilisateurDto> {
    return this.http.get<UtilisateurDto>(`${this.apiUrl}/email/${email}`);
  }

  // Récupérer les utilisateurs actifs
  getActiveUsers(): Observable<UtilisateurDto[]> {
    return this.http.get<UtilisateurDto[]>(`${this.apiUrl}/actifs`);
  }

  // Récupérer les statistiques des utilisateurs
  getUserStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
