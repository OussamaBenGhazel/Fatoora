import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  ApiService,
  PurchaseRequestDto,
  PurchaseRequestLineDto,
  PurchaseOrderDto,
  PurchaseOrderLineDto,
  GoodsReceiptDto,
  GoodsReceiptLineDto,
} from '../core/services/api.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

interface Department {
  id: number;
  code: string;
  name: string;
  nature?: string;
}

interface Supplier {
  id: number;
  name: string;
}

interface Article {
  id: number;
  code: string;
  name: string;
}

@Component({
  selector: 'app-purchase',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './purchase.component.html',
  styleUrl: './purchase.component.scss',
})
export class PurchaseComponent implements OnInit {
  private readonly apiBase = environment.apiUrl;

  view: 'requests' | 'orders' | 'receipts' = 'requests';

  departments: Department[] = [];
  suppliers: Supplier[] = [];
  articles: Article[] = [];

  requests: PurchaseRequestDto[] = [];
  orders: PurchaseOrderDto[] = [];
  receipts: GoodsReceiptDto[] = [];

  // New request
  newRequestDepartmentId: number | null = null;
  newRequestNeededDate: string | null = null;
  newRequestLines: PurchaseRequestLineDto[] = [];
  newRequestArticleId: number | null = null;
  newRequestQuantity: number | null = null;

  // New order
  newOrderSupplierId: number | null = null;
  newOrderDepartmentId: number | null = null;
  newOrderRequestId: number | null = null;
  newOrderDate: string | null = null;
  newOrderLines: PurchaseOrderLineDto[] = [];
  newOrderArticleId: number | null = null;
  newOrderQuantity: number | null = null;
  newOrderUnitPrice: number | null = null;

  // New receipt
  newReceiptOrderId: number | null = null;
  newReceiptDate: string | null = null;
  newReceiptLines: GoodsReceiptLineDto[] = [];
  newReceiptArticleId: number | null = null;
  newReceiptQuantity: number | null = null;
  newReceiptUnitPrice: number | null = null;

  loading = false;
  error = '';

  constructor(
    private api: ApiService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadReferenceData();
    this.reloadAll();
  }

  setView(view: 'requests' | 'orders' | 'receipts'): void {
    this.view = view;
  }

  private loadReferenceData(): void {
    this.http.get<Department[]>(`${this.apiBase}/param/stock/departments`).subscribe((d) => (this.departments = d));
    this.http.get<Supplier[]>(`${this.apiBase}/param/suppliers`).subscribe((s) => (this.suppliers = s));
    this.http.get<Article[]>(`${this.apiBase}/param/articles`).subscribe((a) => (this.articles = a));
  }

  private reloadAll(): void {
    this.loading = true;
    this.error = '';
    this.api.getPurchaseRequests().subscribe((reqs) => (this.requests = reqs));
    this.api.getPurchaseOrders().subscribe((ords) => (this.orders = ords));
    this.api.getGoodsReceipts().subscribe((rcs) => {
      this.receipts = rcs;
      this.loading = false;
    });
  }

  // --- Requests ---

  addRequestLine(): void {
    if (!this.newRequestArticleId || !this.newRequestQuantity) return;
    this.newRequestLines.push({
      articleId: this.newRequestArticleId,
      quantity: this.newRequestQuantity,
      comment: '',
    });
    this.newRequestArticleId = null;
    this.newRequestQuantity = null;
  }

  createRequest(): void {
    if (!this.newRequestDepartmentId || this.newRequestLines.length === 0) return;
    const payload = {
      departmentId: this.newRequestDepartmentId,
      neededDate: this.newRequestNeededDate,
      lines: this.newRequestLines,
    };
    this.api.createPurchaseRequest(payload).subscribe({
      next: () => {
        this.newRequestLines = [];
        this.newRequestNeededDate = null;
        this.newRequestDepartmentId = null;
        this.reloadAll();
      },
      error: () => (this.error = 'Failed to create request'),
    });
  }

  submitRequest(req: PurchaseRequestDto): void {
    this.api.updatePurchaseRequestStatus(req.id, 'SUBMITTED').subscribe({
      next: () => this.reloadAll(),
      error: () => (this.error = 'Failed to submit request'),
    });
  }

  approveRequest(req: PurchaseRequestDto): void {
    this.api.updatePurchaseRequestStatus(req.id, 'APPROVED').subscribe({
      next: () => this.reloadAll(),
      error: () => (this.error = 'Failed to approve request'),
    });
  }

  // --- Orders ---

  addOrderLine(): void {
    if (!this.newOrderArticleId || !this.newOrderQuantity || !this.newOrderUnitPrice) return;
    this.newOrderLines.push({
      articleId: this.newOrderArticleId,
      quantity: this.newOrderQuantity,
      unitPrice: this.newOrderUnitPrice,
      discountPercent: 0,
    });
    this.newOrderArticleId = null;
    this.newOrderQuantity = null;
    this.newOrderUnitPrice = null;
  }

  createOrder(): void {
    if (!this.newOrderSupplierId || this.newOrderLines.length === 0) return;
    const payload = {
      supplierId: this.newOrderSupplierId,
      departmentId: this.newOrderDepartmentId,
      requestId: this.newOrderRequestId,
      orderDate: this.newOrderDate,
      lines: this.newOrderLines,
    };
    this.api.createPurchaseOrder(payload).subscribe({
      next: () => {
        this.newOrderLines = [];
        this.newOrderSupplierId = null;
        this.newOrderDepartmentId = null;
        this.newOrderRequestId = null;
        this.newOrderDate = null;
        this.reloadAll();
      },
      error: () => (this.error = 'Failed to create order'),
    });
  }

  // --- Receipts ---

  addReceiptLine(): void {
    if (!this.newReceiptArticleId || !this.newReceiptQuantity || !this.newReceiptUnitPrice) return;
    this.newReceiptLines.push({
      articleId: this.newReceiptArticleId,
      quantity: this.newReceiptQuantity,
      unitPrice: this.newReceiptUnitPrice,
    });
    this.newReceiptArticleId = null;
    this.newReceiptQuantity = null;
    this.newReceiptUnitPrice = null;
  }

  createReceipt(): void {
    if (!this.newReceiptOrderId || this.newReceiptLines.length === 0) return;
    const payload = {
      orderId: this.newReceiptOrderId,
      receiptDate: this.newReceiptDate,
      lines: this.newReceiptLines,
    };
    this.api.createGoodsReceipt(payload).subscribe({
      next: () => {
        this.newReceiptLines = [];
        this.newReceiptOrderId = null;
        this.newReceiptDate = null;
        this.reloadAll();
      },
      error: () => (this.error = 'Failed to create receipt'),
    });
  }
}

