import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = `${environment.apiUrl}/evenements`;
  private utilisateursUrl = `${environment.apiUrl}/utilisateurs`;

  constructor(private http: HttpClient) {}

  getEventStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`).pipe(
      catchError(this.handleError)
    );
  }

  getActiveEvents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/actifs`).pipe(
      map(events => {
        if (!Array.isArray(events)) {
          throw new Error('Les données reçues ne sont pas un tableau');
        }
        return events.map(event => {
          console.log('Événement reçu - Prix:', event.prix, 'Type:', typeof event.prix);
          return {
            id: event.id,
            titre: event.titre || 'Événement sans titre',
            description: event.description,
            dateDebut: new Date(event.dateDebut),
            dateFin: new Date(event.dateFin),
            placesDisponibles: event.placesDisponibles || 0,
            capaciteMax: event.placesMax || 0,
            placesReservees: event.placesReservees || 0,
            prix: Number(event.prix) || 0,
            categorie: event.categorie,
            lieu: event.lieu || 'Lieu non spécifié',
            statut: event.statut || 'PLANIFIE',
            imageUrl: event.imageUrl,
            chiffreAffaires: Number(event.chiffreAffaires) || 0,
            organisateur: event.organisateur
          };
        });
      }),
      catchError(this.handleError)
    );
  }

  searchEvents(searchTerm: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search?q=${encodeURIComponent(searchTerm)}`).pipe(
      map(events => {
        if (!Array.isArray(events)) {
          throw new Error('Les données reçues ne sont pas un tableau');
        }
        return events.map(event => {
          console.log('Événement reçu - Prix:', event.prix, 'Type:', typeof event.prix);
          return {
            id: event.id,
            titre: event.titre || 'Événement sans titre',
            description: event.description,
            dateDebut: new Date(event.dateDebut),
            dateFin: new Date(event.dateFin),
            placesDisponibles: event.placesDisponibles || 0,
            capaciteMax: event.placesMax || 0,
            placesReservees: event.placesReservees || 0,
            prix: Number(event.prix) || 0, // Convertir en nombre
            categorie: event.categorie,
            lieu: event.lieu || 'Lieu non spécifié',
            statut: event.statut || 'PLANIFIE',
            imageUrl: event.imageUrl,
            chiffreAffaires: Number(event.chiffreAffaires) || 0,
            organisateur: event.organisateur
          };
        });
      }),
      catchError(this.handleError)
    );
  }

  trouverParId(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(event => {
        console.log('Événement individuel reçu - Prix:', event.prix, 'Type:', typeof event.prix);
        return {
          id: event.id,
          titre: event.titre || 'Événement sans titre',
          description: event.description,
          dateDebut: new Date(event.dateDebut),
          dateFin: new Date(event.dateFin),
          placesDisponibles: event.placesDisponibles || 0,
          capaciteMax: event.placesMax || 0,
          placesReservees: event.placesReservees || 0,
          prix: Number(event.prix) || 0,
          categorie: event.categorie,
          lieu: event.lieu || 'Lieu non spécifié',
          statut: event.statut || 'PLANIFIE',
          imageUrl: event.imageUrl,
          chiffreAffaires: Number(event.chiffreAffaires) || 0,
          organisateur: event.organisateur
        };
      }),
      catchError(this.handleError)
    );
  }

  getOrganisateurs(): Observable<any[]> {
    return this.http.get<any[]>(this.utilisateursUrl).pipe(
      map(users => users.map(user => ({
        id: user.id,
        nomComplet: user.nomComplet || user.email,
        email: user.email
      }))),
      catchError(this.handleError)
    );
  }

  createEvent(event: any): Observable<any> {
    const { organisateurId, capaciteMax, ...eventData } = event;
    return this.http.post<any>(`${this.apiUrl}`, eventData).pipe(
      catchError(this.handleError)
    );
  }

  updateEvent(id: number, event: any): Observable<any> {
    const { organisateurId, capaciteMax, ...eventData } = event;
    return this.http.put<any>(`${this.apiUrl}/${id}`, eventData).pipe(
      catchError(this.handleError)
    );
  }

  deleteEvent(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getEventReservations(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/reservations?evenementId=${id}`).pipe(
      catchError(this.handleError)
    );
  }
reserveEvent(eventId: number, token: string): Observable<any> {
    const payload = {
      evenementId: eventId,
      nombrePlaces: 1
    };
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.post('http://localhost:8080/reservations', payload, { headers });
  }
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error('Erreur dans EventService:', error);
    let errorMessage = 'Une erreur inconnue s\'est produite';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      errorMessage = `Code d'erreur: ${error.status}, Message: ${error.message}`;
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      }
      if (error.status === 400 && error.error.errors) {
        errorMessage = error.error.errors.map((err: any) => err.defaultMessage).join('; ');
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}