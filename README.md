# ClinicCare

ClinicCare is a clinic management application with a Spring Boot API and a
React web client.

## Project layout

- `backend/` - Spring Boot REST API, database migrations, and backend tests.
- `cliniccare-frontend/` - React/Vite web application.
- `documentation/backend-configuration.md` - backend configuration and
  integration reference.

## Requirements

- Java 21 or later
- PostgreSQL
- Node.js and npm

## Run locally

Create a PostgreSQL database named `cliniccare`, then open PowerShell in the
repository root. Set the database and application secrets in that terminal:

```powershell
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/cliniccare"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_PASSWORD = "<your PostgreSQL password>"
$env:JWT_SECRET = "<a random secret of at least 48 characters>"
$env:CLINICCARE_BOOTSTRAP_DOCTOR_USERNAME = "<doctor username>"
$env:CLINICCARE_BOOTSTRAP_DOCTOR_PASSWORD = "<strong doctor password>"
```

Replace the example values; do not commit real credentials. Start the backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Flyway applies pending database migrations at startup. The API listens on
`http://localhost:8081` by default, with its API base at
`http://localhost:8081/api/v1`.

The health endpoint is `http://localhost:8081/api/v1/health`. For an Android
emulator, use `http://10.0.2.2:8081/api/v1` as the API base URL.

In another PowerShell terminal, configure and start the frontend:

```powershell
cd cliniccare-frontend
npm install
npm run dev
```

Set `VITE_API_BASE_URL=http://localhost:8081/api/v1` in
`cliniccare-frontend/.env.development` and open `http://localhost:5173`.
See [the frontend guide](cliniccare-frontend/README.md) for user-facing
features and more setup notes.

## API authorization

Login, token refresh, and WhatsApp webhook endpoints are public. Other API
routes require an `Authorization: Bearer ACCESS_TOKEN` header. Doctor-only
routes require the `DOCTOR` role.

For a separate Android client, store tokens using Android Keystore-backed
storage, lock the app after inactivity, and reauthenticate with a PIN or
biometric check. On `401`, refresh tokens and retry a request at most once.
Download PDFs using authenticated requests and keep them in app-private
storage.

## Validation

Run backend tests:

```powershell
cd backend
.\mvnw.cmd test
```

Run frontend lint and production build:

```powershell
cd cliniccare-frontend
npm run lint
npm run build
```


