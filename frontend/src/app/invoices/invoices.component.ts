import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService, InvoiceDto, CompanyAccountDto } from '../core/services/api.service';

@Component({
  selector: 'app-invoices',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.scss',
})
export class InvoicesComponent {
  accountId: number | null = null;
  accounts: CompanyAccountDto[] = [];
  invoices: InvoiceDto[] = [];
  totalElements = 0;
  page = 0;
  size = 20;
  loading = false;
  error = '';
  sendLoadingId: number | null = null;

  showCreateModal = false;
  newDocId = '';
  newXml = '';

  constructor(
    private route: ActivatedRoute,
    private api: ApiService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const id = params['accountId'];
      this.accountId = id ? +id : null;
      if (this.accountId) this.loadInvoices();
    });
    this.api.getClients().subscribe((clients) => {
      if (clients.length > 0) {
        this.api.getAccounts(clients[0].id).subscribe((acc) => {
          this.accounts = acc;
          if (!this.accountId && acc.length > 0) {
            this.accountId = acc[0].id;
            this.loadInvoices();
          }
        });
      }
    });
  }

  loadInvoices(): void {
    if (!this.accountId) return;
    this.loading = true;
    this.error = '';
    this.api.getInvoices(this.accountId, this.page, this.size).subscribe({
      next: (res) => {
        this.invoices = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load invoices';
        this.loading = false;
      },
    });
  }

  onSend(id: number): void {
    this.sendLoadingId = id;
    this.api.sendInvoice(id).subscribe({
      next: (res) => {
        this.sendLoadingId = null;
        if (res.error) this.error = res.error;
        else this.loadInvoices();
      },
      error: (err) => {
        this.sendLoadingId = null;
        this.error = err.error?.error || 'Send failed';
        this.loadInvoices();
      },
    });
  }

  openCreate(): void {
    this.showCreateModal = true;
    this.newDocId = '';
    this.newXml = '<Invoice><DocumentIdentifier></DocumentIdentifier></Invoice>';
  }

  closeCreate(): void {
    this.showCreateModal = false;
  }

  createInvoice(): void {
    if (!this.accountId || !this.newDocId.trim()) return;
    this.api.createInvoice(this.accountId, this.newDocId.trim(), this.newXml.trim()).subscribe({
      next: () => {
        this.closeCreate();
        this.loadInvoices();
      },
      error: (err) => (this.error = err.error?.message || 'Create failed'),
    });
  }

  statusClass(status: string): string {
    const s = (status || '').toLowerCase();
    if (s === 'accepted' || s === 'sent') return 'status-ok';
    if (s === 'rejected' || s === 'error') return 'status-error';
    return 'status-pending';
  }
}
