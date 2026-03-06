import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export interface ClientDto {
  id: number;
  name: string;
  matriculeFiscale: string;
  maxInvoicesPerDay: number;
}

export interface CompanyAccountDto {
  id: number;
  clientId: number;
  name: string;
  accountCode: string;
}

export interface InvoiceDto {
  id: number;
  companyAccountId: number;
  documentIdentifier: string;
  status: string;
  errorMessage: string | null;
  sentAt: string | null;
  responseReceivedAt: string | null;
  createdAt: string;
}

export interface AuditLogDto {
  id: number;
  action: string;
  entityType: string;
  entityId: string;
  username: string;
  details: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly api = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) {}

  getClients(): Observable<ClientDto[]> {
    return this.http.get<ClientDto[]>(`${this.api}/clients`);
  }

  getClient(id: number): Observable<ClientDto> {
    return this.http.get<ClientDto>(`${this.api}/clients/${id}`);
  }

  getAccounts(clientId: number): Observable<CompanyAccountDto[]> {
    return this.http.get<CompanyAccountDto[]>(`${this.api}/clients/${clientId}/accounts`);
  }

  getInvoices(companyAccountId: number, page = 0, size = 20): Observable<{ content: InvoiceDto[]; totalElements: number }> {
    const params = new HttpParams()
      .set('companyAccountId', companyAccountId)
      .set('page', page)
      .set('size', size);
    return this.http.get<{ content: InvoiceDto[]; totalElements: number }>(`${this.api}/invoices`, { params });
  }

  getInvoice(id: number): Observable<InvoiceDto> {
    return this.http.get<InvoiceDto>(`${this.api}/invoices/${id}`);
  }

  createInvoice(companyAccountId: number, documentIdentifier: string, xmlContent: string): Observable<InvoiceDto> {
    return this.http.post<InvoiceDto>(`${this.api}/invoices`, {
      companyAccountId,
      documentIdentifier,
      xmlContent
    });
  }

  sendInvoice(id: number): Observable<{ status?: string; error?: string }> {
    return this.http.post<{ status?: string; error?: string }>(`${this.api}/invoices/${id}/send`, {});
  }

  getAuditLogs(params: { from?: string; to?: string; username?: string; page?: number; size?: number }): Observable<{ content: AuditLogDto[]; totalElements: number }> {
    let httpParams = new HttpParams();
    if (params.from) httpParams = httpParams.set('from', params.from);
    if (params.to) httpParams = httpParams.set('to', params.to);
    if (params.username) httpParams = httpParams.set('username', params.username);
    if (params.page != null) httpParams = httpParams.set('page', params.page);
    if (params.size != null) httpParams = httpParams.set('size', params.size);
    return this.http.get<{ content: AuditLogDto[]; totalElements: number }>(`${this.api}/audit`, { params: httpParams });
  }
}
