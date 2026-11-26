# Backend configuration

The backend reads configuration from
`backend/src/main/resources/application.properties`. Values written as
`${NAME:default}` can be overridden with the matching environment variable.
Set credentials in the process environment or deployment secret manager;
never commit secrets.

## Database and server

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8081` | HTTP port for the API |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/cliniccare` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | `postgres` | Database user |
| `DATABASE_PASSWORD` | empty | Database password; set this before connecting to PostgreSQL |

Hibernate validates the schema (`ddl-auto=validate`); it does not create or
update the schema. Flyway is enabled and applies versioned migrations during
startup. The API health check is at `/api/v1/health`. Actuator exposes the
`health` and `info` endpoints.

## Authentication and clinic details

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `JWT_SECRET` | development placeholder | Signing key; replace with a random secret of at least 48 characters |
| `JWT_ACCESS_MINUTES` | `15` | Access-token lifetime |
| `JWT_REFRESH_DAYS` | `7` | Refresh-token lifetime |
| `CLINIC_NAME` | `ClinicCare` | Clinic name used in application output |
| `CLINIC_CONTACT` | empty | Clinic contact information |
| `CLINIC_ADDRESS` | empty | Clinic address |
| `FRONTEND_URL` | `http://localhost:5173` | Frontend origin, including password-recovery links |
| `CLINICCARE_BOOTSTRAP_DOCTOR_USERNAME` | empty | Optional initial doctor username |
| `CLINICCARE_BOOTSTRAP_DOCTOR_PASSWORD` | empty | Optional initial doctor password |
| `CLINICCARE_BOOTSTRAP_DOCTOR_EMAIL` | empty | Optional initial doctor email |

Bootstrap credentials are used to create the initial doctor account when
configured. Use a strong password and remove these values from the environment
after initial setup.

## Email and password recovery

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `CLINICCARE_MAIL_ENABLED` | `false` | Enables outgoing recovery email |
| `CLINICCARE_MAIL_FROM` | `no-reply@cliniccare.local` | Sender address |
| `MAIL_HOST` | empty | SMTP server |
| `MAIL_PORT` | `587` | SMTP port |
| `MAIL_USERNAME` | empty | SMTP user |
| `MAIL_PASSWORD` | empty | SMTP password |
| `MAIL_SMTP_AUTH` | `true` | SMTP authentication |
| `MAIL_SMTP_STARTTLS` | `true` | STARTTLS |

Enable recovery only after valid SMTP details and a real sender address have
been configured.

## Patient images and uploads

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `CLINICCARE_UPLOAD_DIR` | `${user.home}/.cliniccare/patient-images` | Private image storage directory |

The configured per-file upload limit is 5 MB, and the total multipart request
limit is 11 MB. Keep the upload directory outside the web root and restrict
filesystem access to the backend service account.

## WhatsApp

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `WHATSAPP_ACCESS_TOKEN` | empty | Meta API access token |
| `WHATSAPP_PHONE_NUMBER_ID` | empty | WhatsApp phone-number ID |
| `WHATSAPP_BUSINESS_ACCOUNT_ID` | empty | WhatsApp Business Account ID |
| `WHATSAPP_VERIFY_TOKEN` | empty | Secret token used for the webhook GET challenge |
| `WHATSAPP_APP_SECRET` | empty | App secret used to validate signed webhook POSTs |
| `WHATSAPP_GRAPH_VERSION` | `v22.0` | Meta Graph API version |
| `WHATSAPP_TEMPLATE_NAME` | `appointment_reminder` | Approved reminder template name |

Set both webhook secrets before configuring Meta. Use this callback URL:

```text
https://<public-backend-host>/api/v1/webhooks/whatsapp
```

The backend must be publicly reachable over HTTPS; `localhost` is not
reachable by Meta. In Meta's WhatsApp webhook settings, use the same
`WHATSAPP_VERIFY_TOKEN` value and subscribe to the `messages` field. Incoming
messages are persisted and displayed in the authenticated WhatsApp Inbox.
Dashboard test events can be used while the Meta app is unpublished; production
events require the app to be published.

## Local PowerShell example

Set only the values needed for your environment in the same terminal that
starts the backend:

```powershell
$env:DATABASE_PASSWORD = "<your PostgreSQL password>"
$env:JWT_SECRET = "<a random secret of at least 48 characters>"
$env:CLINICCARE_BOOTSTRAP_DOCTOR_USERNAME = "<doctor username>"
$env:CLINICCARE_BOOTSTRAP_DOCTOR_PASSWORD = "<strong doctor password>"
.\mvnw.cmd spring-boot:run
```
