ALTER TABLE patients ALTER COLUMN patient_code TYPE VARCHAR(64);

UPDATE patients
SET patient_code = 'LEGACY-' || id;

WITH numbered_patients AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY registration_date, created_at, id) AS serial_number
    FROM patients
)
UPDATE patients AS p
SET patient_code = numbered_patients.serial_number::text
FROM numbered_patients
WHERE p.id = numbered_patients.id;

ALTER TABLE patients ALTER COLUMN patient_code TYPE VARCHAR(30);

CREATE SEQUENCE patient_registration_seq START WITH 1;

SELECT setval(
    'patient_registration_seq',
    GREATEST(COUNT(*), 1),
    COUNT(*) > 0
)
FROM patients;
