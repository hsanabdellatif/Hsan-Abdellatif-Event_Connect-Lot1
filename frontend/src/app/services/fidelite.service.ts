import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Utilisateur {
  id: number;
  email: string;
  pointsFidelite: number;
  totalReservations: number;
  totalEvenementsOrganises: number;
  nom: string;
  prenom: string;
  telephone: string;
  role: string;
  niveauFidelite: string;
}

export interface Badge {
  id: number;
  nom: string;
  description: string;
  pointsRequis: number;
  type: string;
  icone?: string;
  couleur?: string;
  actif: boolean;
}

export interface UtilisateurBadge {
  id: number;
  badge: Badge;
  dateObtention: string;
  pointsAuMomentObtention: number;
  commentaire: string;
}

@Injectable({
  providedIn: 'root'
})
export class FideliteService {
  private apiUrl = `${environment.apiUrl}/fidelite`;

  constructor(private http: HttpClient) {}

  getProfilFidelite(utilisateurId: number): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.apiUrl}/profil/${utilisateurId}`);
  }

  getBadgesUtilisateur(utilisateurId: number): Observable<UtilisateurBadge[]> {
    return this.http.get<UtilisateurBadge[]>(`${this.apiUrl}/badges/${utilisateurId}`);
  }

  getProchainsBadges(utilisateurId: number): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${this.apiUrl}/prochains-badges/${utilisateurId}`);
  }

  getTousLesBadges(): Observable<Badge[]> {
    return this.http.get<Badge[]>(`${this.apiUrl}/badges`);
  }
}