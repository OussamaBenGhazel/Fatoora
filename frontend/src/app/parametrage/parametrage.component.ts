import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, ClientDto } from '../core/services/api.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

interface ArticleFamily {
  id?: number;
  code: string;
  name: string;
  description?: string;
}

interface ArticleSubFamily {
  id?: number;
  code: string;
  name: string;
  description?: string;
  family: ArticleFamily | null;
}

interface Article {
  id?: number;
  code: string;
  name: string;
  unit?: string;
  purchasePrice?: number;
  salePrice?: number;
  stockQuantity?: number;
  active: boolean;
  subFamily: ArticleSubFamily | null;
}

interface ClientCategory {
  id?: number;
  code: string;
  name: string;
  description?: string;
}

interface SupplierCategory {
  id?: number;
  code: string;
  name: string;
  description?: string;
}

interface Supplier {
  id?: number;
  name: string;
  matriculeFiscale?: string;
  email?: string;
  phone?: string;
  address?: string;
  category?: SupplierCategory | null;
}

@Component({
  selector: 'app-parametrage',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './parametrage.component.html',
  styleUrl: './parametrage.component.scss',
})
export class ParametrageComponent {
  private readonly apiBase = environment.apiUrl;

  tab: 'articles' | 'clients' | 'suppliers' = 'articles';

  families: ArticleFamily[] = [];
  subFamilies: ArticleSubFamily[] = [];
  articles: Article[] = [];

  clientCategories: ClientCategory[] = [];
  supplierCategories: SupplierCategory[] = [];
  suppliers: Supplier[] = [];

  newFamily: ArticleFamily = { code: '', name: '', description: '' };
  newSubFamily: ArticleSubFamily = { code: '', name: '', description: '', family: null };
  newArticle: Article = { code: '', name: '', active: true, subFamily: null };
  newClientCategory: ClientCategory = { code: '', name: '' };
  newSupplierCategory: SupplierCategory = { code: '', name: '' };
  newSupplier: Supplier = { name: '' };

  loading = false;
  error = '';

  constructor(private http: HttpClient, private api: ApiService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  setTab(tab: 'articles' | 'clients' | 'suppliers'): void {
    this.tab = tab;
  }

  loadAll(): void {
    this.loading = true;
    this.error = '';

    // Articles parametrage
    this.http.get<ArticleFamily[]>(`${this.apiBase}/param/articles/families`).subscribe((f) => (this.families = f));
    this.http
      .get<ArticleSubFamily[]>(`${this.apiBase}/param/articles/sub-families`)
      .subscribe((sf) => (this.subFamilies = sf));
    this.http.get<Article[]>(`${this.apiBase}/param/articles`).subscribe((a) => (this.articles = a));

    // Client & supplier parametrage
    this.http
      .get<ClientCategory[]>(`${this.apiBase}/param/client-categories`)
      .subscribe((c) => (this.clientCategories = c));
    this.http
      .get<SupplierCategory[]>(`${this.apiBase}/param/supplier-categories`)
      .subscribe((c) => (this.supplierCategories = c));
    this.http.get<Supplier[]>(`${this.apiBase}/param/suppliers`).subscribe((s) => {
      this.suppliers = s;
      this.loading = false;
    });
  }

  // --- Create handlers (simplified to add-only for now) ---

  addFamily(): void {
    if (!this.newFamily.code || !this.newFamily.name) return;
    this.http.post<ArticleFamily>(`${this.apiBase}/param/articles/families`, this.newFamily).subscribe((f) => {
      this.families.push(f);
      this.newFamily = { code: '', name: '', description: '' };
    });
  }

  addSubFamily(): void {
    if (!this.newSubFamily.code || !this.newSubFamily.name || !this.newSubFamily.family) return;
    this.http
      .post<ArticleSubFamily>(`${this.apiBase}/param/articles/sub-families`, this.newSubFamily)
      .subscribe((sf) => {
        this.subFamilies.push(sf);
        this.newSubFamily = { code: '', name: '', description: '', family: null };
      });
  }

  addArticle(): void {
    if (!this.newArticle.code || !this.newArticle.name || !this.newArticle.subFamily) return;
    this.http.post<Article>(`${this.apiBase}/param/articles`, this.newArticle).subscribe((a) => {
      this.articles.push(a);
      this.newArticle = { code: '', name: '', active: true, subFamily: null };
    });
  }

  addClientCategory(): void {
    if (!this.newClientCategory.code || !this.newClientCategory.name) return;
    this.http
      .post<ClientCategory>(`${this.apiBase}/param/client-categories`, this.newClientCategory)
      .subscribe((c) => {
        this.clientCategories.push(c);
        this.newClientCategory = { code: '', name: '' };
      });
  }

  addSupplierCategory(): void {
    if (!this.newSupplierCategory.code || !this.newSupplierCategory.name) return;
    this.http
      .post<SupplierCategory>(`${this.apiBase}/param/supplier-categories`, this.newSupplierCategory)
      .subscribe((c) => {
        this.supplierCategories.push(c);
        this.newSupplierCategory = { code: '', name: '' };
      });
  }

  addSupplier(): void {
    if (!this.newSupplier.name) return;
    this.http.post<Supplier>(`${this.apiBase}/param/suppliers`, this.newSupplier).subscribe((s) => {
      this.suppliers.push(s);
      this.newSupplier = { name: '' };
    });
  }
}

