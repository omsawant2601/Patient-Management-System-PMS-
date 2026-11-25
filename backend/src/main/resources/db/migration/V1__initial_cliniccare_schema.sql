
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1. Application users
CREATE TABLE app_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    mobile VARCHAR(15) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('DOCTOR', 'RECEPTIONIST')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. Patients
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_code VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    address TEXT,
    contact_number VARCHAR(15),
    date_of_birth DATE,
    age INTEGER CHECK (age BETWEEN 0 AND 130),
    created_by UUID REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patients_name
ON patients (LOWER(full_name));

CREATE INDEX idx_patients_mobile
ON patients (mobile);

-- 3. Appointments
CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_number VARCHAR(40) NOT NULL UNIQUE,
    patient_id UUID NOT NULL REFERENCES patients(id),
    scheduled_at TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 15,
    appointment_type VARCHAR(30) DEFAULT 'CONSULTATION',
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
        CHECK (status IN (
            'SCHEDULED',
            'CONFIRMED',
            'COMPLETED',
            'CANCELLED',
            'NO_SHOW'
        )),
    notes TEXT,
    created_by UUID REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_appointments_scheduled
ON appointments (scheduled_at, status);

CREATE INDEX idx_appointments_patient
ON appointments (patient_id, scheduled_at DESC);

-- 4. Medical records
CREATE TABLE medical_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    appointment_id UUID REFERENCES appointments(id),
    doctor_id UUID NOT NULL REFERENCES app_users(id),
    visit_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    symptoms TEXT,
    diagnosis TEXT,
    clinical_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_medical_records_patient
ON medical_records (patient_id, visit_date DESC);

-- 5. Prescriptions
CREATE TABLE prescriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medical_record_id UUID NOT NULL
        REFERENCES medical_records(id),
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL REFERENCES app_users(id),
    prescription_number VARCHAR(40) NOT NULL UNIQUE,
    instructions TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'FINALIZED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 6. Prescription medicines
CREATE TABLE prescription_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID NOT NULL
        REFERENCES prescriptions(id),
    medicine_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    duration VARCHAR(100),
    quantity VARCHAR(50),
    instructions TEXT
);

-- 7. Patient WhatsApp consent
CREATE TABLE notification_consents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    channel VARCHAR(20) NOT NULL,
    consent_given BOOLEAN NOT NULL DEFAULT FALSE,
    consent_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    UNIQUE (patient_id, channel)
);

-- 8. Appointment reminders
CREATE TABLE appointment_reminders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_id UUID NOT NULL
        REFERENCES appointments(id),
    reminder_type VARCHAR(20) NOT NULL
        CHECK (reminder_type IN ('24_HOUR', '1_HOUR')),
    scheduled_at TIMESTAMPTZ NOT NULL,
    sent_at TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN (
            'PENDING',
            'PROCESSING',
            'SENT',
            'FAILED',
            'CANCELLED'
        )),
    provider_message_id VARCHAR(150),
    retry_count INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    UNIQUE (appointment_id, reminder_type)
);

CREATE INDEX idx_reminders_due
ON appointment_reminders (scheduled_at, status);

-- 9. Audit logs
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES app_users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);