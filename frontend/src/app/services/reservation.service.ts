import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environment.apiUrl}/reservations`;

  constructor(private http: HttpClient) { }

  getAllReservations(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(reservations => {
        if (!Array.isArray(reservations)) {
          throw new Error('Les données reçues ne sont pas un tableau');
        }
        return reservations.map(reservation => ({
          id: reservation.id,
          utilisateur: {
            nomComplet: reservation.utilisateur?.nomComplet || reservation.utilisateur?.email || 'Utilisateur inconnu',
            email: reservation.utilisateur?.email || 'N/A'
          },
          evenement: {
            titre: reservation.evenement?.titre || 'Événement inconnu'
          },
          statut: reservation.statut || 'EN_ATTENTE',
          dateReservation: reservation.dateReservation ? new Date(reservation.dateReservation) : new Date(),
          nombrePlaces: reservation.nombrePlaces || 1,
          montantTotal: reservation.montantTotal || 0
        }));
      }),
      catchError(this.handleError)
    );
  }

  getReservationStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`).pipe(
      catchError(this.handleError)
    );
  }

  getHistoricalStats(period: 'DAILY' | 'MONTHLY', startDate: string, endDate: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/historique?period=${period}&startDate=${startDate}&endDate=${endDate}`).pipe(
      catchError(this.handleError)
    );
  }

  confirmerReservation(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/confirmer`, {}).pipe(
      catchError(this.handleError)
    );
  }

  annulerReservation(id: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${id}/annuler`, {}).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Erreur dans ReservationService:', error);
    let errorMessage = 'Une erreur inconnue s\'est produite';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      errorMessage = error.error?.message || `Code d'erreur: ${error.status}, Message: ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }
}