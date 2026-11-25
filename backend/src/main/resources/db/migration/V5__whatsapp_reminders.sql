ALTER TABLE notification_consents
    ADD COLUMN IF NOT EXISTS consent_status VARCHAR(20) NOT NULL DEFAULT 'DENIED',
    ADD COLUMN IF NOT EXISTS consent_source VARCHAR(100),
    ADD COLUMN IF NOT EXISTS contact_number VARCHAR(15);

ALTER TABLE appointment_reminders
    ADD COLUMN IF NOT EXISTS last_attempt_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS next_attempt_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS delivered_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS read_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE appointment_reminders DROP CONSTRAINT IF EXISTS appointment_reminders_status_check;
ALTER TABLE appointment_reminders ADD CONSTRAINT appointment_reminders_status_check
    CHECK (status IN ('PENDING','PROCESSING','SENT','DELIVERED','READ','FAILED','CANCELLED'));

CREATE TABLE whatsapp_webhook_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_event_id VARCHAR(150),
    provider_message_id VARCHAR(150),
    event_type VARCHAR(100) NOT NULL,
    payload JSONB,
    received_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(provider_event_id)
);
CREATE INDEX IF NOT EXISTS idx_whatsapp_webhook_message
    ON whatsapp_webhook_events(provider_message_id);
