# ClinicCare React Frontend

## Run locally

```powershell
cd E:\Programs\cliniccare\cliniccare-frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

Start the backend in another terminal:

```powershell
cd E:\Programs\cliniccare\backend
$env:CLINICCARE_BOOTSTRAP_DOCTOR_USERNAME = "your-doctor-username"
$env:CLINICCARE_BOOTSTRAP_DOCTOR_PASSWORD = "enter-a-strong-password-here"
.\mvnw.cmd spring-boot:run
```

The backend listens on port `8081` by default. The bootstrap variables create
the first doctor account only when the username does not already exist. Set
real values only in your local PowerShell session; do not commit them. The
currently running local database has no user accounts, so authenticated pages
require this initial doctor setup before they can be exercised.

Configure `.env.development`:

```text
VITE_API_BASE_URL=http://localhost:8081/api/v1
```

After changing `.env.development`, restart Vite with `Ctrl+C` and `npm run dev`
so Vite reads the updated API base URL.

## Existing backend integrations

- Authentication: login, refresh, logout and change-password
- Users: list, create, view, update and activate/deactivate
- Patients: list, search, create, update, detail and full profile history (appointments, consultations and prescriptions); patient registration numbers are assigned sequentially
- Appointments: list, create, reschedule, cancel and status; appointment and prescription identifiers include the patient name and that patient's serial number
- Consultations: create, get, update and patient history
- Prescriptions: create, update, finalize, history and PDF download
- Reminders: global list and appointment reminders
- WhatsApp inbox: incoming messages, auto-refreshed every 30 seconds

## Missing or mismatched capabilities

- No global consultation-list endpoint: consultations are loaded for the selected patient.
- No dashboard aggregate endpoint: dashboard figures are derived from patient, appointment and reminder responses.
- No dedicated reporting endpoint: report charts use currently available patient and appointment data.
- No prescription revision/amendment endpoint is currently exposed.
- The backend does not issue HttpOnly refresh cookies; refresh tokens are currently stored in localStorage and access tokens in memory.

Do not use this application against production until secrets, HTTPS, cookie-based refresh handling and remaining aggregate/report endpoints are implemented and verified.

## Password recovery

The login page links to `/forgot-password`; the email link opens `/reset-password`.
The backend sends one-time links only when mail delivery is enabled and the
account has an email address. Configure these backend environment variables
before enabling recovery:

- `CLINICCARE_MAIL_ENABLED=true`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `MAIL_SMTP_AUTH=true`, `MAIL_SMTP_STARTTLS=true`
- `CLINICCARE_MAIL_FROM`
- `FRONTEND_URL=http://localhost:5173`

Recovery tokens are stored hashed, expire after 30 minutes, are single-use, and
invalidate existing refresh tokens after a successful reset. Without SMTP
configuration, the endpoint responds with `503` rather than claiming that an
email was sent.

## WhatsApp webhooks

Configure the Meta callback URL as
`https://<public-backend-host>/api/v1/webhooks/whatsapp`. The backend must be
reachable over public HTTPS; `localhost:8081` cannot receive Meta callbacks.
Set `WHATSAPP_VERIFY_TOKEN` to a private random string and enter the same value
as the Verify Token in the Meta dashboard. Also set `WHATSAPP_APP_SECRET` so
the POST endpoint can validate Meta's `X-Hub-Signature-256` header.

In the Meta app's WhatsApp webhook configuration, subscribe to the `messages`
field to receive inbound messages and delivery statuses. Incoming message
content is stored in the database and appears in the authenticated WhatsApp
Inbox; outgoing appointment reminder statuses remain on the WhatsApp Reminders
page. While the app is unpublished, Meta only sends dashboard test events.
Publish the app before expecting production WhatsApp data. Use a secure HTTPS
tunnel for local testing.

The WhatsApp access token is read from `WHATSAPP_ACCESS_TOKEN`, not source
configuration. If an earlier token was exposed, revoke it and issue a new one
in Meta before connecting the app.

## Patient images

Patient profile pictures and paired before-and-after comparisons are stored
outside the web root in the backend user's private
`~/.cliniccare/patient-images` directory by default. Override the location with
`CLINICCARE_UPLOAD_DIR`. Image reads require authentication; uploads accept
JPEG and PNG files up to 5 MB each, with both comparison images required.
Flyway migration V8 updates existing appointment and prescription numbers and
adds the patient-image metadata table.

## Production build

```powershell
npm run build
npm run preview
```
