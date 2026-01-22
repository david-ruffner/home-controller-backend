-- init.sql
-- This file is for YOUR custom SQL. It runs only on the very first
-- initialization of the data directory (i.e., when /var/lib/mysql is empty).
-- The database, user, and grants are handled via docker-compose env vars.

-- Example: create a table or seed data (safe to leave commented out)
USE homecontrols;

-- Corresponds to EmailNotificationSettings in the UML
CREATE TABLE favorite_colors (
    favorite_color_id VARCHAR(64) NOT NULL,
    light_id VARCHAR(128) NULL,
    group_id VARCHAR(128) NULL,
    color VARCHAR(64) NOT NULL,

    PRIMARY KEY (favorite_color_id)
);