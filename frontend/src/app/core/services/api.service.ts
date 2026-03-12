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

export interface PurchaseRequestLineDto {
  articleId: number;
  quantity: number;
  comment?: string;
}

export interface PurchaseRequestDto {
  id: number;
  departmentId: number | null;
  departmentName?: string;
  requestedBy: string;
  neededDate: string | null;
  status: string;
  createdAt: string;
}

export interface PurchaseOrderLineDto {
  articleId: number;
  quantity: number;
  unitPrice: number;
  discountPercent?: number;
}

export interface PurchaseOrderDto {
  id: number;
  supplierId: number;
  supplierName?: string;
  departmentId: number | null;
  departmentName?: string;
  orderDate: string | null;
  status: string;
}

export interface GoodsReceiptLineDto {
  articleId: number;
  quantity: number;
  unitPrice: number;
}

export interface GoodsReceiptDto {
  id: number;
  orderId: number;
  receiptDate: string | null;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly api = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) {}

  getClients(): Observable<ClientDto[]> {
    // Returns only the clients visible for the current user (managing company vs normal client vs admin).
    return this.http.get<ClientDto[]>(`${this.api}/clients/current`);
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

  // --- Purchase (Achat) ---

  getPurchaseRequests(): Observable<PurchaseRequestDto[]> {
    return this.http.get<PurchaseRequestDto[]>(`${this.api}/purchase/requests`);
  }

  createPurchaseRequest(payload: { departmentId: number; neededDate?: string | null; lines: PurchaseRequestLineDto[] }): Observable<PurchaseRequestDto> {
    return this.http.post<PurchaseRequestDto>(`${this.api}/purchase/requests`, payload);
  }

  updatePurchaseRequestStatus(id: number, status: string): Observable<PurchaseRequestDto> {
    const params = new HttpParams().set('status', status);
    return this.http.post<PurchaseRequestDto>(`${this.api}/purchase/requests/${id}/status`, {}, { params });
  }

  getPurchaseOrders(): Observable<PurchaseOrderDto[]> {
    return this.http.get<PurchaseOrderDto[]>(`${this.api}/purchase/orders`);
  }

  createPurchaseOrder(payload: {
    supplierId: number;
    departmentId?: number | null;
    requestId?: number | null;
    orderDate?: string | null;
    lines: PurchaseOrderLineDto[];
  }): Observable<PurchaseOrderDto> {
    return this.http.post<PurchaseOrderDto>(`${this.api}/purchase/orders`, payload);
  }

  getGoodsReceipts(): Observable<GoodsReceiptDto[]> {
    return this.http.get<GoodsReceiptDto[]>(`${this.api}/purchase/receipts`);
  }

  createGoodsReceipt(payload: { orderId: number; receiptDate?: string | null; lines: GoodsReceiptLineDto[] }): Observable<GoodsReceiptDto> {
    return this.http.post<GoodsReceiptDto>(`${this.api}/purchase/receipts`, payload);
  }
}
