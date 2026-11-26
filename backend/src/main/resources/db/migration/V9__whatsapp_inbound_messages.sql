CREATE TABLE whatsapp_inbound_messages (
    id UUID PRIMARY KEY,
    provider_message_id VARCHAR(150) NOT NULL UNIQUE,
    sender_phone VARCHAR(32) NOT NULL,
    sender_name VARCHAR(150),
    message_type VARCHAR(40) NOT NULL,
    content TEXT NOT NULL,
    received_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_whatsapp_inbound_received_at
    ON whatsapp_inbound_messages(received_at DESC);
