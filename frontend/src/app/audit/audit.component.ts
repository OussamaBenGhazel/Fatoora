import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, AuditLogDto } from '../core/services/api.service';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit.component.html',
  styleUrl: './audit.component.scss',
})
export class AuditComponent {
  logs: AuditLogDto[] = [];
  totalElements = 0;
  page = 0;
  size = 20;
  loading = false;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getAuditLogs({ page: this.page, size: this.size }).subscribe({
      next: (res) => {
        this.logs = res.content ?? [];
        this.totalElements = res.totalElements ?? 0;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  prev(): void {
    if (this.page > 0) {
      this.page--;
      this.load();
    }
  }

  next(): void {
    if ((this.page + 1) * this.size < this.totalElements) {
      this.page++;
      this.load();
    }
  }
}
