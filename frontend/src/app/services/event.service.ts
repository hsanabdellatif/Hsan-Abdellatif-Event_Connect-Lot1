import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface EventDto {
  id?: number;
  nom: string;
  description: string;
  dateDebut: string;
  dateFin: string;
  lieu: string;
  nombrePlaces: number;
  prix: number;
  statut: string;
  placesReservees?: number;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = `${environment.apiUrl}/evenements`;

  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // Récupérer tous les événements
  getAllEvents(): Observable<EventDto[]> {
    return this.http.get<EventDto[]>(this.apiUrl);
  }

  // Récupérer un événement par ID
  getEventById(id: number): Observable<EventDto> {
    return this.http.get<EventDto>(`${this.apiUrl}/${id}`);
  }

  // Créer un nouvel événement
  createEvent(event: EventDto): Observable<EventDto> {
    return this.http.post<EventDto>(this.apiUrl, event, this.httpOptions);
  }

  // Mettre à jour un événement
  updateEvent(id: number, event: EventDto): Observable<EventDto> {
    return this.http.put<EventDto>(`${this.apiUrl}/${id}`, event, this.httpOptions);
  }

  // Supprimer un événement
  deleteEvent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Rechercher des événements
  searchEvents(searchTerm: string): Observable<EventDto[]> {
    return this.http.get<EventDto[]>(`${this.apiUrl}/search?q=${searchTerm}`);
  }

  // Récupérer les événements actifs
  getActiveEvents(): Observable<EventDto[]> {
    return this.http.get<EventDto[]>(`${this.apiUrl}/actifs`);
  }

  // Récupérer les statistiques des événements
  getEventStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
