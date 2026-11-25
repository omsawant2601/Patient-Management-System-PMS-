ALTER TABLE app_users ADD COLUMN email VARCHAR(254);

CREATE UNIQUE INDEX ux_app_users_email_lower
    ON app_users (LOWER(email))
    WHERE email IS NOT NULL;
