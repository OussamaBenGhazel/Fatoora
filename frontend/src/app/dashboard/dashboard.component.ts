import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { ApiService, ClientDto, CompanyAccountDto } from '../core/services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  private auth = inject(AuthService);
  private api = inject(ApiService);

  /** Expose the auth user signal so the template can call user(). */
  user = this.auth.user;
  clients: ClientDto[] = [];
  accounts: CompanyAccountDto[] = [];
  selectedClientId: number | null = null;

  ngOnInit(): void {
    this.api.getClients().subscribe((list) => {
      this.clients = list;
      if (list.length > 0 && this.selectedClientId == null) {
        this.selectClient(list[0].id);
      }
    });
  }

  selectClient(id: number): void {
    this.selectedClientId = id;
    this.api.getAccounts(id).subscribe((list) => (this.accounts = list));
  }

  logout(): void {
    this.auth.logout();
  }
}
