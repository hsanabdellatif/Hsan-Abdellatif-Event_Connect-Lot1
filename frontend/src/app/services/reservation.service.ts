import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ReservationDto {
  id?: number;
  utilisateurId: number;
  evenementId: number;
  nombrePlaces: number;
  prixTotal: number;
  statut: string;
  dateReservation?: string;
  utilisateurNom?: string;
  utilisateurEmail?: string;
  evenementNom?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservations`;

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // Récupérer toutes les réservations
  getAllReservations(): Observable<ReservationDto[]> {
    return this.http.get<ReservationDto[]>(this.apiUrl);
  }

  // Récupérer une réservation par ID
  getReservationById(id: number): Observable<ReservationDto> {
    return this.http.get<ReservationDto>(`${this.apiUrl}/${id}`);
  }

  // Créer une nouvelle réservation
  createReservation(reservation: ReservationDto): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(this.apiUrl, reservation, this.httpOptions);
  }

  // Mettre à jour une réservation
  updateReservation(id: number, reservation: ReservationDto): Observable<ReservationDto> {
    return this.http.put<ReservationDto>(`${this.apiUrl}/${id}`, reservation, this.httpOptions);
  }

  // Supprimer une réservation
  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Confirmer une réservation
  confirmReservation(id: number): Observable<ReservationDto> {
    return this.http.patch<ReservationDto>(`${this.apiUrl}/${id}/confirmer`, {}, this.httpOptions);
  }

  // Annuler une réservation
  cancelReservation(id: number): Observable<ReservationDto> {
    return this.http.patch<ReservationDto>(`${this.apiUrl}/${id}/annuler`, {}, this.httpOptions);
  }

  // Récupérer les réservations par événement
  getReservationsByEvent(eventId: number): Observable<ReservationDto[]> {
    return this.http.get<ReservationDto[]>(`${this.apiUrl}/evenement/${eventId}`);
  }

  // Récupérer les réservations par utilisateur
  getReservationsByUser(userId: number): Observable<ReservationDto[]> {
    return this.http.get<ReservationDto[]>(`${this.apiUrl}/utilisateur/${userId}`);
  }

  // Récupérer les réservations par statut
  getReservationsByStatus(status: string): Observable<ReservationDto[]> {
    return this.http.get<ReservationDto[]>(`${this.apiUrl}/statut/${status}`);
  }

  // Récupérer les statistiques des réservations
  getReservationStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
