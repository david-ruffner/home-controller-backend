-- init.sql
-- This file is for YOUR custom SQL. It runs only on the very first
-- initialization of the data directory (i.e., when /var/lib/mysql is empty).
-- The database, user, and grants are handled via docker-compose env vars.

-- Example: create a table or seed data (safe to leave commented out)
CREATE DATABASE homecontrols;

USE homecontrols;

-- Corresponds to EmailNotificationSettings in the UML
CREATE TABLE favorite_colors (
    favorite_color_id VARCHAR(64) NOT NULL,
    light_id VARCHAR(128) NULL,
    group_id VARCHAR(128) NULL,
    color VARCHAR(64) NOT NULL,

    PRIMARY KEY (favorite_color_id)
);

CREATE TABLE category (
    category_id VARCHAR(64) NOT NULL,
    category_name VARCHAR(500) NOT NULL,

    PRIMARY KEY (category_id)
);

CREATE TABLE forecast_data (
    forecast_data_id VARCHAR(64) NOT NULL,
    type VARCHAR(12) NOT NULL,
    forecast_data TEXT NOT NULL,
    generated_time TIMESTAMP NOT NULL,

    PRIMARY KEY (forecast_data_id)
);

CREATE TABLE item (
    item_id VARCHAR(64) NOT NULL,
    item_name VARCHAR(500) NOT NULL,
    category_id VARCHAR(64) NOT NULL,
    description TEXT,
    quantity LONG,
    upc VARCHAR(64) NOT NULL,
    room_id VARCHAR(64),
    container_id VARCHAR(64),
    quantity_threshold LONG NOT NULL,

    PRIMARY KEY (item_id)
);

CREATE TABLE item_container (
    container_id VARCHAR(64) NOT NULL,
    container_name VARCHAR(1000) NOT NULL,

    PRIMARY KEY (container_id)
);

CREATE TABLE light_bulbs (
    light_bulb_id VARCHAR(64) NOT NULL,
    light_id VARCHAR(64) NOT NULL,
    device_id VARCHAR(64) NOT NULL,
    brightness DOUBLE NOT NULL,
    red DOUBLE NOT NULL,
    green DOUBLE NOT NULL,
    blue DOUBLE NOT NULL,
    is_on BOOL NOT NULL,
    name VARCHAR(1000) NOT NULL,

    PRIMARY KEY (light_bulb_id)
);

CREATE TABLE room (
    room_id VARCHAR(64) NOT NULL,
    room_name VARCHAR(500) NOT NULL,

    PRIMARY KEY (room_id)
);

CREATE TABLE user_settings (
    username VARCHAR(1000) NOT NULL,
    address VARCHAR(512) NOT NULL,
    lat VARCHAR(64),
    lon VARCHAR(64),
    weather_api_key VARCHAR(128),
    hue_api_key VARCHAR(128),
    todoist_api_key VARCHAR(128),
    geoapify_key VARCHAR(128),
    control_device_id VARCHAR(128),
    city VARCHAR(128),
    state VARCHAR(128),
    state_code VARCHAR(8),
    time_zone VARCHAR(128),
    name VARCHAR(128),
    pin_number VARCHAR(64),
    account_type VARCHAR(32),
    inbox_project_id VARCHAR(64),

    PRIMARY KEY (username)
);