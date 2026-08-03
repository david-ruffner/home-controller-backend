-- init.sql
-- Runs only on the very first initialization of the data directory
-- (i.e., when /var/lib/mysql is empty).
-- The database, user, and grants are created via MYSQL_* env vars in .env / docker-compose.
-- Tables are created in MYSQL_DATABASE (default selected by the MySQL entrypoint).

CREATE TABLE favorite_colors (
    favorite_color_id VARCHAR(64) NOT NULL,
    room_id VARCHAR(128) NOT NULL,
    color VARCHAR(64) NOT NULL,
    timestamp TIMESTAMP NOT NULL,

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
    category_id VARCHAR(64) NULL,
    description TEXT NULL,
    quantity BIGINT NOT NULL,
    upc VARCHAR(64) NOT NULL,
    room_id VARCHAR(64) NULL,
    container_id VARCHAR(64) NULL,
    quantity_threshold BIGINT NOT NULL,

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
    name VARCHAR(1000) NOT NULL,
    is_on INT NOT NULL,
    room_id VARCHAR(128) NOT NULL,

    PRIMARY KEY (light_bulb_id)
);

CREATE TABLE room (
    room_id VARCHAR(64) NOT NULL,
    room_name VARCHAR(500) NOT NULL,
    group_id VARCHAR(128) NOT NULL,

    PRIMARY KEY (room_id)
);

CREATE TABLE user_settings (
    user_settings_id VARCHAR(64) NOT NULL,
    address VARCHAR(512) NULL,
    lat VARCHAR(64) NULL,
    lon VARCHAR(64) NULL,
    weather_api_key VARCHAR(128) NULL,
    hue_api_key VARCHAR(128) NULL,
    todoist_api_key VARCHAR(128) NULL,
    geoapify_key VARCHAR(128) NULL,
    city VARCHAR(128) NULL,
    state VARCHAR(128) NULL,
    state_code VARCHAR(8) NULL,
    time_zone VARCHAR(128) NULL,
    name VARCHAR(128) NOT NULL,
    pin_number VARCHAR(64) NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    username VARCHAR(255) NOT NULL,
    inbox_project_id VARCHAR(64) NULL,
    control_device_id VARCHAR(128) NOT NULL,

    PRIMARY KEY (user_settings_id)
);

CREATE TABLE inventory_item(
    item_id VARCHAR(64) NOT NULL,
    item_name VARCHAR(1000) NOT NULL,
    item_description TEXT NULL,
    room_id VARCHAR(64) NULL,
    container_id VARCHAR(64) NULL,
    upc VARCHAR(256) NOT NULL,
    current_quantity LONG NOT NULL,
    quantity_threshold LONG NULL,
    notify_on_threshold BOOL NOT NULL,
    favorite_id VARCHAR(64) NULL,

    PRIMARY KEY (item_id)
);

CREATE TABLE inventory_room(
    room_id VARCHAR(64) NOT NULL,
    room_name VARCHAR(1000) NOT NULL,

    PRIMARY KEY (room_id)
);

CREATE TABLE inventory_container(
    container_id VARCHAR(64) NOT NULL,
    room_id VARCHAR(64) NOT NULL,
    container_name VARCHAR(1000) NOT NULL,

    PRIMARY KEY (container_id)
);

CREATE TABLE inventory_favorites(
    favorite_id VARCHAR(64) NOT NULL,

    PRIMARY KEY (favorite_id)
);

CREATE TABLE inventory_category(
    category_id VARCHAR(64) NOT NULL,
    category_name VARCHAR(1000) NOT NULL,

    PRIMARY KEY (category_id)
);

CREATE TABLE inventory_tags(
    tag_id VARCHAR(64) NOT NULL,
    tag_name VARCHAR(1000) NOT NULL,

    PRIMARY KEY (tag_id)
);

CREATE TABLE inventory_tag_inventory_item(
    tag_item_id VARCHAR(64) NOT NULL,
    tag_id VARCHAR(64) NOT NULL,
    item_id VARCHAR(64) NOT NULL,

    PRIMARY KEY (tag_item_id)
);

INSERT INTO user_settings (
    user_settings_id, address, lat, lon, weather_api_key, hue_api_key, todoist_api_key, geoapify_key,
    city, state, state_code, time_zone, name, pin_number, account_type, username,
    inbox_project_id, control_device_id
) VALUES
(
'c49c50a42ef2b5ca46ffc5b1298d6cd3c1d61174d72da97c6322740ab7896f9d',
    '22717 Alger St, St Clair Shores, MI',
    '42.455212758169935',
    '-82.8954289738562',
    'none',
    'JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ',
    'fdaf',
    '3ed43a1069884e3383a3c52e177f6acf',
    'Saint Clair Shores',
    'Michigan',
    'MI',
    'America/Detroit',
    'David',
    '82f1fc68c02f9f616f47296d1d2c9242e387944e47d0647e35eb26fe0b1ee31a',
    'admin',
    'dave',
    '6CrfFFPx2gMJgHVC',
    'fjda8jfda8jfdasfhaf'
),
(
'65e946117cdb55f7d4c5af61be36b41dbd1c62ca7d4589a539150f2dc4cf46a6',
    '22717 Alger St, St Clair Shores, MI',
    '42.455212758169935',
    '-82.8954289738562',
    'none',
    'JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ',
    'fdaf',
    '3ed43a1069884e3383a3c52e177f6acf',
    'Saint Clair Shores',
    'Michigan',
    'MI',
    'America/Detroit',
    'Erika',
    '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',
    'standard',
    'erika',
    NULL,
    'fjda8jfda8jfdasfhaf'
),
(
 '851ab0399802b4ec0be0df927e87b5a6eefda8c44ae93311f15fe336d7095436',
    '22717 Alger St, St Clair Shores, MI',
    '42.455212758169935',
    '-82.8954289738562',
    'none',
    'JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ',
    'fdaf',
    '3ed43a1069884e3383a3c52e177f6acf',
    'Saint Clair Shores',
    'Michigan',
    'MI',
    'America/Detroit',
    'Lilly',
    '0ffe1abd1a08215353c233d6e009613e95eec4253832a761af28ff37ac5a150c',
    'standard',
    'tigerlilly',
    NULL,
    'fjda8jfda8jfdasfhaf'
),
(
 '4e47de56f81fd5f239f801662424fb2998f830938f30f16c137eba70aa780c95',
    '22717 Alger St, St Clair Shores, MI',
    '42.455212758169935',
    '-82.8954289738562',
    'none',
    'JmUsSn0cIUtwGZAf4Z6rxmMFsQJPNxMAye7uS4iQ',
    'fdaf',
    '3ed43a1069884e3383a3c52e177f6acf',
    'Saint Clair Shores',
    'Michigan',
    'MI',
    'America/Detroit',
    'Sarah',
    '9af15b336e6a9619928537df30b2e6a2376569fcf9d7e773eccede65606529a0',
    'standard',
    'tinkerbell',
    NULL,
    'fjda8jfda8jfdasfhaf'
);
