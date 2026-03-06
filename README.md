# El Fatoora Supplier

Web application for suppliers to send and monitor electronic invoices with the El Fatoora platform (Tunisia TradeNet).

## Stack

- **Backend:** Spring Boot 3, Java 17, JPA/H2, Spring Security (HTTP Basic), EU DSS (signing), JSch (SFTP)
- **Frontend:** Angular 18, standalone components, SCSS
- **Deployment:** Linux server (backend + frontend static)

## Project layout

```
ProjetFatoora/
├── backend/          # Spring Boot API
├── frontend/         # Angular SPA
└── README.md
```

## Quick start

### Backend

```bash
cd backend
mvn spring-boot:run
```

- API base: **http://localhost:8080/api**
- H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/elfatoora`, user: `sa`, password empty)
- Default user: **admin** / **admin**
- Demo client and one company account are created on first run.

### Frontend

```bash
cd frontend
npm install
npm start
```

- App: **http://localhost:4200**
- Login with **admin** / **admin**, then use Dashboard, Invoices, Audit logs.

## Configuration (backend)

- **application.yml** (or env vars):
  - `elfatoora.sftp.*` – SFTP host, port, user, key path, matricule fiscale (when you have them)
  - `elfatoora.signing.*` – Keystore (token) path, password, key alias for ANCE certificate
  - `elfatoora.limits.max-invoices-per-day-per-client` – default 100, parametrable per client in DB

- **Clients and accounts:** Stored in H2; add more via H2 console or future admin UI. Each client has a **Matricule Fiscale** and optional company accounts (SFTP subfolders).

## Implemented so far

- Simple login (HTTP Basic), one default user
- Clients and company accounts (CRUD via JPA; demo data on startup)
- Invoices: create (manual XML), list by account, “Send” (sign + SFTP upload placeholder)
- Audit logs: list with pagination
- Daily invoice limit per client (parametrable)
- Placeholders: **Signing** (DSS XAdES-B once token is configured), **SFTP** (real upload/poll when credentials are set)

## Next steps

1. **TEIF:** When you have the invoice table schema, add mapping and TEIF XML generation from DB or form.
2. **Signing:** Configure ANCE token (PKCS#11) and complete `SigningService.signWithDss()` with DSS.
3. **SFTP:** Set `elfatoora.sftp.*` and implement real upload/poll in `SftpService`.
4. **Multi-tenant:** Use `companyAccountId` (and optional client filter) everywhere; already supported in the data model.

## Build for production

- **Backend:** `mvn -DskipTests package` → run `java -jar target/elfatoora-supplier-1.0.0-SNAPSHOT.jar`
- **Frontend:** `npm run build` → serve `dist/frontend/browser` (e.g. Nginx on Linux)
