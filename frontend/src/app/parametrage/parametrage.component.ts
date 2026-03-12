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

interface Department {
  id?: number;
  code: string;
  name: string;
  nature?: string;
}

interface UnitOfMeasure {
  id?: number;
  code: string;
  name: string;
  abbreviation?: string;
}

interface ArticleDepartment {
  id?: number;
  article: Article | null;
  department: Department | null;
  dosage?: number;
  salePrice?: number;
  stockMin?: number;
  stockMax?: number;
}

interface ArticleSupplier {
  id?: number;
  article: Article | null;
  supplier: Supplier | null;
  purchasePrice?: number;
  discountPercent?: number;
  conventionDate?: string;
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

  tab: 'articles' | 'clients' | 'suppliers' | 'stock' = 'articles';

  families: ArticleFamily[] = [];
  subFamilies: ArticleSubFamily[] = [];
  articles: Article[] = [];

  departments: Department[] = [];
  units: UnitOfMeasure[] = [];
  articleDepartments: ArticleDepartment[] = [];
  articleSuppliers: ArticleSupplier[] = [];

  clientCategories: ClientCategory[] = [];
  supplierCategories: SupplierCategory[] = [];
  suppliers: Supplier[] = [];

  newFamily: ArticleFamily = { code: '', name: '', description: '' };
  newSubFamily: ArticleSubFamily = { code: '', name: '', description: '', family: null };
  newArticle: Article = { code: '', name: '', active: true, subFamily: null };
  newClientCategory: ClientCategory = { code: '', name: '' };
  newSupplierCategory: SupplierCategory = { code: '', name: '' };
  newSupplier: Supplier = { name: '' };

  newDepartment: Department = { code: '', name: '', nature: '' };
  newUnit: UnitOfMeasure = { code: '', name: '', abbreviation: '' };
  newArticleDept: ArticleDepartment = { article: null, department: null };
  newArticleSupplier: ArticleSupplier = { article: null, supplier: null };

  loading = false;
  error = '';

  constructor(private http: HttpClient, private api: ApiService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  setTab(tab: 'articles' | 'clients' | 'suppliers' | 'stock'): void {
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

    // Stock parametrage
    this.http.get<Department[]>(`${this.apiBase}/param/stock/departments`).subscribe((d) => (this.departments = d));
    this.http.get<UnitOfMeasure[]>(`${this.apiBase}/param/stock/units`).subscribe((u) => (this.units = u));
    this.http
      .get<ArticleDepartment[]>(`${this.apiBase}/param/stock/article-departments`)
      .subscribe((ads) => (this.articleDepartments = ads));
    this.http
      .get<ArticleSupplier[]>(`${this.apiBase}/param/stock/article-suppliers`)
      .subscribe((as) => (this.articleSuppliers = as));

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

  addDepartment(): void {
    if (!this.newDepartment.code || !this.newDepartment.name) return;
    this.http.post<Department>(`${this.apiBase}/param/stock/departments`, this.newDepartment).subscribe((d) => {
      this.departments.push(d);
      this.newDepartment = { code: '', name: '', nature: '' };
    });
  }

  addUnit(): void {
    if (!this.newUnit.code || !this.newUnit.name) return;
    this.http.post<UnitOfMeasure>(`${this.apiBase}/param/stock/units`, this.newUnit).subscribe((u) => {
      this.units.push(u);
      this.newUnit = { code: '', name: '', abbreviation: '' };
    });
  }

  addArticleDepartment(): void {
    if (!this.newArticleDept.article || !this.newArticleDept.department) return;
    const payload: ArticleDepartment = {
      article: { id: this.newArticleDept.article.id, code: '', name: '', active: true, subFamily: null },
      department: { id: this.newArticleDept.department.id, code: '', name: '' },
      dosage: this.newArticleDept.dosage,
      salePrice: this.newArticleDept.salePrice,
      stockMin: this.newArticleDept.stockMin,
      stockMax: this.newArticleDept.stockMax,
    };
    this.http
      .post<ArticleDepartment>(`${this.apiBase}/param/stock/article-departments`, payload)
      .subscribe((ad) => {
        this.articleDepartments.push(ad);
        this.newArticleDept = { article: null, department: null };
      });
  }

  addArticleSupplier(): void {
    if (!this.newArticleSupplier.article || !this.newArticleSupplier.supplier) return;
    const payload: ArticleSupplier = {
      article: { id: this.newArticleSupplier.article.id, code: '', name: '', active: true, subFamily: null },
      supplier: { id: this.newArticleSupplier.supplier.id, name: '' },
      purchasePrice: this.newArticleSupplier.purchasePrice,
      discountPercent: this.newArticleSupplier.discountPercent,
      conventionDate: this.newArticleSupplier.conventionDate,
    };
    this.http
      .post<ArticleSupplier>(`${this.apiBase}/param/stock/article-suppliers`, payload)
      .subscribe((as) => {
        this.articleSuppliers.push(as);
        this.newArticleSupplier = { article: null, supplier: null };
      });
  }
}

