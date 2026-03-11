import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './auth/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { DashboardHomeComponent } from './dashboard/dashboard-home.component';
import { InvoicesComponent } from './invoices/invoices.component';
import { AuditComponent } from './audit/audit.component';
import { ParametrageComponent } from './parametrage/parametrage.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', component: DashboardHomeComponent },
      { path: 'invoices', component: InvoicesComponent },
      { path: 'audit', component: AuditComponent },
      { path: 'parametrage', component: ParametrageComponent },
    ],
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: 'dashboard' },
];
