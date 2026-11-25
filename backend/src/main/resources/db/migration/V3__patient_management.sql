ALTER TABLE patients ADD COLUMN IF NOT EXISTS gender VARCHAR(20);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS registration_date DATE NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE patients ADD COLUMN IF NOT EXISTS updated_by UUID REFERENCES app_users(id);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS alternate_contact_number VARCHAR(15);
ALTER TABLE patients DROP CONSTRAINT IF EXISTS patients_mobile_key;
CREATE UNIQUE INDEX IF NOT EXISTS ux_patients_mobile ON patients(mobile);

ALTER TABLE appointments ADD COLUMN IF NOT EXISTS doctor_id UUID REFERENCES app_users(id);
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS follow_up_of UUID REFERENCES appointments(id);
ALTER TABLE appointments ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE medical_records ADD COLUMN IF NOT EXISTS follow_up_recommendations TEXT;
ALTER TABLE medical_records ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';
ALTER TABLE medical_records ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE prescriptions ADD COLUMN IF NOT EXISTS prescription_date TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE prescriptions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE prescriptions ADD COLUMN IF NOT EXISTS finalized_at TIMESTAMPTZ;
ALTER TABLE prescriptions ADD COLUMN IF NOT EXISTS amendment_of UUID REFERENCES prescriptions(id);

CREATE TABLE prescription_revisions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID NOT NULL REFERENCES prescriptions(id),
    revision_number INTEGER NOT NULL,
    snapshot JSONB NOT NULL,
    amended_by UUID REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(prescription_id, revision_number)
);
