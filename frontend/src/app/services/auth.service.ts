import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth';
  private currentUserSubject = new BehaviorSubject<any>(null);
  currentUser: Observable<any> = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Load user from localStorage or API on initialization
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }
setCurrentUser(user: any): void {
    if (user && user.id) {
      localStorage.setItem('currentUser', JSON.stringify(user));
      this.currentUserSubject.next(user);
    } else {
      console.error('Invalid user object:', user);
    }
  }
  
  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  login(email: string, password: string) {
    return this.http.post<any>(`${this.apiUrl}/login`, { 
      email, 
      motDePasse: password // Transformer password en motDePasse pour correspondre au backend
    }).pipe(tap(user => {
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  register(userData: any) {
    // Transformer le champ password en motDePasse pour correspondre au backend
    const transformedData = {
      ...userData,
      motDePasse: userData.password
    };
    delete transformedData.password;
    return this.http.post(`${this.apiUrl}/register`, transformedData);
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return !!this.currentUserValue;
  }

  updateProfile(userData: any) {
    return this.http.put(`${this.apiUrl}/profile`, userData).pipe(
      tap((updatedUser: any) => {
        const currentUser = this.currentUserValue;
        const newUserData = { ...currentUser, ...updatedUser };
        localStorage.setItem('currentUser', JSON.stringify(newUserData));
        this.currentUserSubject.next(newUserData);
      })
    );
  }
}
