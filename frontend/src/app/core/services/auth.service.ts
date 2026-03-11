import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface User {
  username: string;
  displayName: string;
  clientId?: number | null;
  managingCompany?: boolean;
}

export interface LoginResponse {
  success: boolean;
  message?: string;
  user?: User;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiUrl}/auth`;
  private readonly credentialsKey = 'elfatoora_credentials';

  private currentUser = signal<User | null>(null);
  private credentials = signal<{ username: string; password: string } | null>(null);

  user = computed(() => this.currentUser());
  isLoggedIn = computed(() => this.currentUser() !== null);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.restoreCredentials();
  }

  private restoreCredentials(): void {
    try {
      const raw = sessionStorage.getItem(this.credentialsKey);
      if (raw) {
        const cred = JSON.parse(raw) as { username: string; password: string };
        this.credentials.set(cred);
        this.fetchMe().subscribe();
      }
    } catch {
      this.credentials.set(null);
      this.currentUser.set(null);
    }
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/login`, { username, password }).pipe(
      tap((res) => {
        if (res.success && res.user) {
          this.credentials.set({ username, password });
          this.currentUser.set(res.user);
          sessionStorage.setItem(this.credentialsKey, JSON.stringify({ username, password }));
        }
      }),
      catchError(() => of({ success: false, message: 'Invalid credentials' }))
    );
  }

  private fetchMe(): Observable<User | null> {
    return this.http.get<User>(`${this.api}/me`).pipe(
      tap((user) => this.currentUser.set(user)),
      catchError(() => {
        this.currentUser.set(null);
        this.credentials.set(null);
        sessionStorage.removeItem(this.credentialsKey);
        return of(null);
      })
    );
  }

  logout(): void {
    this.currentUser.set(null);
    this.credentials.set(null);
    sessionStorage.removeItem(this.credentialsKey);
    this.router.navigate(['/login']);
  }

  /** Base64-encoded credentials for HTTP Basic (used by HTTP interceptor). */
  getBasicAuthHeader(): string | null {
    const cred = this.credentials();
    if (!cred) return null;
    const b64 = btoa(`${cred.username}:${cred.password}`);
    return `Basic ${b64}`;
  }
}
