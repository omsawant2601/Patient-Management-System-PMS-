ALTER TABLE appointments ALTER COLUMN appointment_number TYPE VARCHAR(200);
ALTER TABLE prescriptions ALTER COLUMN prescription_number TYPE VARCHAR(200);

UPDATE appointments SET appointment_number = 'MIG-' || id;
UPDATE prescriptions SET prescription_number = 'MIG-' || id;

WITH named_patients AS (
    SELECT id, patient_code,
           COALESCE(NULLIF(BTRIM(REGEXP_REPLACE(LOWER(full_name), '[^a-z0-9]+', '-', 'g'), '-'), ''), 'patient') AS slug
    FROM patients
),
patient_tokens AS (
    SELECT id, patient_code, slug, COUNT(*) OVER (PARTITION BY slug) AS same_slug_count
    FROM named_patients
),
numbered_appointments AS (
    SELECT a.id, t.slug, t.patient_code, t.same_slug_count,
           ROW_NUMBER() OVER (PARTITION BY a.patient_id ORDER BY a.scheduled_at, a.created_at, a.id) AS serial_number
    FROM appointments a
    JOIN patient_tokens t ON t.id = a.patient_id
)
UPDATE appointments a
SET appointment_number = 'APT-' || REGEXP_REPLACE(LEFT(n.slug, 80), '-+$', '') ||
        CASE WHEN n.same_slug_count > 1 THEN '-P' || n.patient_code ELSE '' END ||
        '-' || n.serial_number
FROM numbered_appointments n
WHERE a.id = n.id;

WITH named_patients AS (
    SELECT id, patient_code,
           COALESCE(NULLIF(BTRIM(REGEXP_REPLACE(LOWER(full_name), '[^a-z0-9]+', '-', 'g'), '-'), ''), 'patient') AS slug
    FROM patients
),
patient_tokens AS (
    SELECT id, patient_code, slug, COUNT(*) OVER (PARTITION BY slug) AS same_slug_count
    FROM named_patients
),
numbered_prescriptions AS (
    SELECT p.id, t.slug, t.patient_code, t.same_slug_count,
           ROW_NUMBER() OVER (PARTITION BY p.patient_id ORDER BY p.prescription_date, p.created_at, p.id) AS serial_number
    FROM prescriptions p
    JOIN patient_tokens t ON t.id = p.patient_id
)
UPDATE prescriptions p
SET prescription_number = 'RX-' || REGEXP_REPLACE(LEFT(n.slug, 80), '-+$', '') ||
        CASE WHEN n.same_slug_count > 1 THEN '-P' || n.patient_code ELSE '' END ||
        '-' || n.serial_number
FROM numbered_prescriptions n
WHERE p.id = n.id;

ALTER TABLE patients ADD COLUMN profile_photo_key VARCHAR(64);

CREATE TABLE patient_photo_comparisons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    before_image_key VARCHAR(64) NOT NULL,
    after_image_key VARCHAR(64) NOT NULL,
    caption VARCHAR(500),
    created_by UUID REFERENCES app_users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_patient_photo_comparisons_patient_created
    ON patient_photo_comparisons(patient_id, created_at DESC);
