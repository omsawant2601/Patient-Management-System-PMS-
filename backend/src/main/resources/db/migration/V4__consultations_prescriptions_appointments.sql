ALTER TABLE medical_records ADD CONSTRAINT ck_medical_record_status
    CHECK (status IN ('DRAFT','FINALIZED'));
ALTER TABLE prescriptions ADD CONSTRAINT ck_prescription_status
    CHECK (status IN ('DRAFT','FINALIZED','CANCELLED'));
ALTER TABLE prescriptions ALTER COLUMN status SET DEFAULT 'DRAFT';

ALTER TABLE appointments ADD CONSTRAINT ck_appointments_duration
    CHECK (duration_minutes BETWEEN 5 AND 240);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_time
    ON appointments(doctor_id, scheduled_at, status);

ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS details JSONB;
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45);
