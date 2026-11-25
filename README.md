ClinicCare backend setup
========================

See `backend/` for the Spring Boot backend.

Environment variables
---------------------

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- `JWT_SECRET` (at least 48 characters)
- `JWT_ACCESS_MINUTES`, `JWT_REFRESH_DAYS`
- `CLINICCARE_BOOTSTRAP_DOCTOR_USERNAME`, `CLINICCARE_BOOTSTRAP_DOCTOR_PASSWORD`
- `CLINICCARE_MAIL_ENABLED`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `CLINICCARE_MAIL_FROM`, `FRONTEND_URL`
- `WHATSAPP_ACCESS_TOKEN`, `WHATSAPP_PHONE_NUMBER_ID`
- `WHATSAPP_VERIFY_TOKEN`, `WHATSAPP_GRAPH_VERSION`
- `WHATSAPP_TEMPLATE_NAME`

Run locally
-----------

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

API base URL: `http://localhost:8081/api/v1`
Android emulator URL: `http://10.0.2.2:8081/api/v1`

Authorization
-------------

Login, refresh, and webhook endpoints are public; other endpoints require
`Authorization: Bearer ACCESS_TOKEN`. Doctor-only endpoints enforce the
`DOCTOR` role. Clinic records are only visible to authenticated doctors or
the permitted receptionist workflows.

Android integration
-------------------

Store access and refresh tokens in EncryptedSharedPreferences or Android
Keystore-backed storage. Lock the Compose app after inactivity, require
PIN/biometric reauthentication, and reject pending refresh attempts after
lock/logout. On `401`, call `/auth/refresh`, replace the access and refresh
tokens, and retry once. Download PDFs with an authenticated `GET` and persist
the returned PDF bytes only in app-private storage.