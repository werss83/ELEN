------------------------
--- INITIAL DATABASE ---
------------------------

# --- !Ups

CREATE TABLE account (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    first_name VARCHAR(128) NOT NULL,
    last_name VARCHAR(128) NOT NULL,
    email VARCHAR(512) NOT NULL,
    address VARCHAR(512) NOT NULL,
    city VARCHAR(256) NOT NULL,
    zip_code VARCHAR(16) NOT NULL,
    additional_address VARCHAR(256),
    coordinate GEOMETRY(point, 4326) NOT NULL,
    phone_number VARCHAR(16) NOT NULL,
    profile_picture VARCHAR(1024),
    password VARCHAR(256),
    google_id VARCHAR(32),
    facebook_id VARCHAR(32),
    twitter_id VARCHAR(32),
    last_login TIMESTAMPTZ,
    active BOOLEAN NOT NULL,
    email_verified BOOLEAN NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_account_uid UNIQUE (uid),
    CONSTRAINT uq_account_email UNIQUE (email),
    CONSTRAINT uq_account_google_id UNIQUE (google_id),
    CONSTRAINT uq_account_facebook_id UNIQUE (facebook_id),
    CONSTRAINT uq_account_twitter_id UNIQUE (twitter_id),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE booked_care (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    booked_care_type VARCHAR(9) NOT NULL,
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ,
    duration BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT ck_booked_care_booked_care_type CHECK ( booked_care_type IN ('ONE_TIME', 'RECURRENT')),
    CONSTRAINT uq_booked_care_uid UNIQUE (uid),
    CONSTRAINT pk_booked_care PRIMARY KEY (id)
);

CREATE TABLE booked_care_children (
    booked_care_id BIGINT NOT NULL,
    children_id BIGINT NOT NULL,
    CONSTRAINT pk_booked_care_children PRIMARY KEY (booked_care_id, children_id)
);

CREATE TABLE caf_option (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    opt_key VARCHAR(32) NOT NULL,
    opt_value TEXT,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_caf_option_uid UNIQUE (uid),
    CONSTRAINT uq_caf_option_opt_key UNIQUE (opt_key),
    CONSTRAINT pk_caf_option PRIMARY KEY (id)
);

CREATE TABLE care_criteria (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    gender BOOLEAN DEFAULT FALSE NOT NULL,
    have_car BOOLEAN DEFAULT FALSE NOT NULL,
    speaks_english BOOLEAN DEFAULT FALSE NOT NULL,
    experience_with_children BOOLEAN DEFAULT FALSE NOT NULL,
    smoke BOOLEAN DEFAULT FALSE NOT NULL,
    childhood_training BOOLEAN DEFAULT FALSE NOT NULL,
    booked_care_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_care_criteria_uid UNIQUE (uid),
    CONSTRAINT uq_care_criteria_booked_care_id UNIQUE (booked_care_id),
    CONSTRAINT pk_care_criteria PRIMARY KEY (id)
);

CREATE TABLE children (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    first_name VARCHAR(64),
    category VARCHAR(24) NOT NULL,
    birth_date DATE,
    parent_id BIGINT NOT NULL,
    usual_childcare VARCHAR(24),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT ck_children_category CHECK ( category IN ('UNDER_3_YEARS', 'UNDER_6_YEARS', 'OVER_6_YEARS', 'BIRTHDAY')),
    CONSTRAINT ck_children_usual_childcare CHECK ( usual_childcare IN
        ('KINDERGARTEN', 'PUBLIC_NURSERY', 'MATERNAL_ASSISTANCE',
         'PRIVATE_NURSERY', 'OTHER')),
    CONSTRAINT uq_children_uid UNIQUE (uid),
    CONSTRAINT pk_children PRIMARY KEY (id)
);

CREATE TABLE children_has_booked_care (
    children_id BIGINT NOT NULL,
    booked_care_id BIGINT NOT NULL,
    CONSTRAINT pk_children_has_booked_care PRIMARY KEY (children_id, booked_care_id)
);

CREATE TABLE effective_care (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    status VARCHAR(12) NOT NULL,
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ,
    booked_care_id BIGINT NOT NULL,
    effective_unavailability_id BIGINT,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT ck_effective_care_status CHECK ( status IN
        ('PLANNED', 'ASSIGNED', 'PERFORMED', 'CANCELLED', 'UNSUCCESSFUL')),
    CONSTRAINT uq_effective_care_uid UNIQUE (uid),
    CONSTRAINT uq_effective_care_effective_unavailability_id UNIQUE (effective_unavailability_id),
    CONSTRAINT pk_effective_care PRIMARY KEY (id)
);

CREATE TABLE effective_unavailability (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ,
    planned_unavailability_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_effective_unavailability_uid UNIQUE (uid),
    CONSTRAINT pk_effective_unavailability PRIMARY KEY (id)
);

CREATE TABLE parent_criteria (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    gender BOOLEAN DEFAULT FALSE NOT NULL,
    have_car BOOLEAN DEFAULT FALSE NOT NULL,
    speaks_english BOOLEAN DEFAULT FALSE NOT NULL,
    experience_with_children BOOLEAN DEFAULT FALSE NOT NULL,
    smoke BOOLEAN DEFAULT FALSE NOT NULL,
    childhood_training BOOLEAN DEFAULT FALSE NOT NULL,
    parent_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_parent_criteria_uid UNIQUE (uid),
    CONSTRAINT uq_parent_criteria_parent_id UNIQUE (parent_id),
    CONSTRAINT pk_parent_criteria PRIMARY KEY (id)
);

CREATE TABLE parent (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    account_id BIGINT NOT NULL,
    sponsor_code VARCHAR(16) NOT NULL,
    sponsor_id BIGINT,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_parent_uid UNIQUE (uid),
    CONSTRAINT uq_parent_account_id UNIQUE (account_id),
    CONSTRAINT uq_parent_sponsor_code UNIQUE (sponsor_code),
    CONSTRAINT pk_parent PRIMARY KEY (id)
);

CREATE TABLE parent_option (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    opt_key VARCHAR(32) NOT NULL,
    opt_value TEXT,
    parent_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_parent_option_uid UNIQUE (uid),
    CONSTRAINT uq_parent_option_parent_id_opt_key UNIQUE (parent_id, opt_key),
    CONSTRAINT pk_parent_option PRIMARY KEY (id)
);

CREATE TABLE planned_unavailability (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    sitter_unavailability_type VARCHAR(9) NOT NULL,
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ,
    duration BIGINT NOT NULL,
    sitter_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT ck_planned_unavailability_sitter_unavailability_type CHECK ( sitter_unavailability_type IN ('ONE_TIME', 'RECURRENT')),
    CONSTRAINT uq_planned_unavailability_uid UNIQUE (uid),
    CONSTRAINT pk_planned_unavailability PRIMARY KEY (id)
);

CREATE TABLE role (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    role VARCHAR(32) NOT NULL,
    account_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT ck_role_role CHECK ( role IN ('NONE', 'ROLE_PARENT', 'ROLE_SITTER', 'ROLE_ADMIN')),
    CONSTRAINT uq_role_uid UNIQUE (uid),
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE sitter_attribute (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    gender BOOLEAN DEFAULT FALSE NOT NULL,
    have_car BOOLEAN DEFAULT FALSE NOT NULL,
    speaks_english BOOLEAN DEFAULT FALSE NOT NULL,
    experience_with_children BOOLEAN DEFAULT FALSE NOT NULL,
    smoke BOOLEAN DEFAULT FALSE NOT NULL,
    childhood_training BOOLEAN DEFAULT FALSE NOT NULL,
    guard_type VARCHAR[] NOT NULL,
    experiences VARCHAR[] NOT NULL,
    experience_types VARCHAR[] NOT NULL,
    situation VARCHAR(32) NOT NULL,
    situation_detail TEXT,
    certifications VARCHAR[] NOT NULL,
    extra_certification TEXT,
    spoken_languages VARCHAR[] NOT NULL,
    homework_capabilities VARCHAR[] NOT NULL,
    car_situation VARCHAR(24) NOT NULL,
    public_transports VARCHAR[] NOT NULL,
    availabilities VARCHAR[] NOT NULL,
    commendation TEXT,
    information TEXT,
    sitter_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT ck_sitter_attribute_situation CHECK ( situation IN
        ('STUDENT', 'ACTIVE_WITH_CHILDREN', 'ACTIVE_WITHOUT_CHILDREN',
         'SENIOR')),
    CONSTRAINT ck_sitter_attribute_car_situation CHECK ( car_situation IN ('YES', 'NO_BUT_PLANNED', 'NO')),
    CONSTRAINT uq_sitter_attribute_uid UNIQUE (uid),
    CONSTRAINT uq_sitter_attribute_sitter_id UNIQUE (sitter_id),
    CONSTRAINT pk_sitter_attribute PRIMARY KEY (id)
);

CREATE TABLE sitter (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    account_id BIGINT NOT NULL,
    birthday DATE,
    picture BYTEA,
    picture_mime_type VARCHAR(32),
    picture_etag VARCHAR(32),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_sitter_uid UNIQUE (uid),
    CONSTRAINT uq_sitter_account_id UNIQUE (account_id),
    CONSTRAINT pk_sitter PRIMARY KEY (id)
);

CREATE TABLE sitter_option (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    uid UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    opt_key VARCHAR(32) NOT NULL,
    opt_value TEXT,
    sitter_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT uq_sitter_option_uid UNIQUE (uid),
    CONSTRAINT uq_sitter_option_sitter_id_opt_key UNIQUE (sitter_id, opt_key),
    CONSTRAINT pk_sitter_option PRIMARY KEY (id)
);

CREATE INDEX ix_booked_care_parent_id ON booked_care(parent_id);
ALTER TABLE booked_care
    ADD CONSTRAINT fk_booked_care_parent_id FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_booked_care_children_booked_care ON booked_care_children(booked_care_id);
ALTER TABLE booked_care_children
    ADD CONSTRAINT fk_booked_care_children_booked_care FOREIGN KEY (booked_care_id) REFERENCES booked_care(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_booked_care_children_children ON booked_care_children(children_id);
ALTER TABLE booked_care_children
    ADD CONSTRAINT fk_booked_care_children_children FOREIGN KEY (children_id) REFERENCES children(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE care_criteria
    ADD CONSTRAINT fk_care_criteria_booked_care_id FOREIGN KEY (booked_care_id) REFERENCES booked_care(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_children_parent_id ON children(parent_id);
ALTER TABLE children
    ADD CONSTRAINT fk_children_parent_id FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_children_has_booked_care_children ON children_has_booked_care(children_id);
ALTER TABLE children_has_booked_care
    ADD CONSTRAINT fk_children_has_booked_care_children FOREIGN KEY (children_id) REFERENCES children(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_children_has_booked_care_booked_care ON children_has_booked_care(booked_care_id);
ALTER TABLE children_has_booked_care
    ADD CONSTRAINT fk_children_has_booked_care_booked_care FOREIGN KEY (booked_care_id) REFERENCES booked_care(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_effective_care_booked_care_id ON effective_care(booked_care_id);
ALTER TABLE effective_care
    ADD CONSTRAINT fk_effective_care_booked_care_id FOREIGN KEY (booked_care_id) REFERENCES booked_care(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE effective_care
    ADD CONSTRAINT fk_effective_care_effective_unavailability_id FOREIGN KEY (effective_unavailability_id) REFERENCES effective_unavailability(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_effective_unavailability_planned_unavailability_id ON effective_unavailability(planned_unavailability_id);
ALTER TABLE effective_unavailability
    ADD CONSTRAINT fk_effective_unavailability_planned_unavailability_id FOREIGN KEY (planned_unavailability_id) REFERENCES planned_unavailability(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE parent_criteria
    ADD CONSTRAINT fk_parent_criteria_parent_id FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE parent
    ADD CONSTRAINT fk_parent_account_id FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_parent_sponsor_id ON parent(sponsor_id);
ALTER TABLE parent
    ADD CONSTRAINT fk_parent_sponsor_id FOREIGN KEY (sponsor_id) REFERENCES parent(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_parent_option_parent_id ON parent_option(parent_id);
ALTER TABLE parent_option
    ADD CONSTRAINT fk_parent_option_parent_id FOREIGN KEY (parent_id) REFERENCES parent(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_planned_unavailability_sitter_id ON planned_unavailability(sitter_id);
ALTER TABLE planned_unavailability
    ADD CONSTRAINT fk_planned_unavailability_sitter_id FOREIGN KEY (sitter_id) REFERENCES sitter(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_role_account_id ON role(account_id);
ALTER TABLE role
    ADD CONSTRAINT fk_role_account_id FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE sitter_attribute
    ADD CONSTRAINT fk_sitter_attribute_sitter_id FOREIGN KEY (sitter_id) REFERENCES sitter(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE sitter
    ADD CONSTRAINT fk_sitter_account_id FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE INDEX ix_sitter_option_sitter_id ON sitter_option(sitter_id);
ALTER TABLE sitter_option
    ADD CONSTRAINT fk_sitter_option_sitter_id FOREIGN KEY (sitter_id) REFERENCES sitter(id) ON DELETE RESTRICT ON UPDATE RESTRICT;


# --- !Downs

ALTER TABLE IF EXISTS booked_care
    DROP CONSTRAINT IF EXISTS fk_booked_care_parent_id;
DROP INDEX IF EXISTS ix_booked_care_parent_id;

ALTER TABLE IF EXISTS booked_care_children
    DROP CONSTRAINT IF EXISTS fk_booked_care_children_booked_care;
DROP INDEX IF EXISTS ix_booked_care_children_booked_care;

ALTER TABLE IF EXISTS booked_care_children
    DROP CONSTRAINT IF EXISTS fk_booked_care_children_children;
DROP INDEX IF EXISTS ix_booked_care_children_children;

ALTER TABLE IF EXISTS care_criteria
    DROP CONSTRAINT IF EXISTS fk_care_criteria_booked_care_id;

ALTER TABLE IF EXISTS children
    DROP CONSTRAINT IF EXISTS fk_children_parent_id;
DROP INDEX IF EXISTS ix_children_parent_id;

ALTER TABLE IF EXISTS children_has_booked_care
    DROP CONSTRAINT IF EXISTS fk_children_has_booked_care_children;
DROP INDEX IF EXISTS ix_children_has_booked_care_children;

ALTER TABLE IF EXISTS children_has_booked_care
    DROP CONSTRAINT IF EXISTS fk_children_has_booked_care_booked_care;
DROP INDEX IF EXISTS ix_children_has_booked_care_booked_care;

ALTER TABLE IF EXISTS effective_care
    DROP CONSTRAINT IF EXISTS fk_effective_care_booked_care_id;
DROP INDEX IF EXISTS ix_effective_care_booked_care_id;

ALTER TABLE IF EXISTS effective_care
    DROP CONSTRAINT IF EXISTS fk_effective_care_effective_unavailability_id;

ALTER TABLE IF EXISTS effective_unavailability
    DROP CONSTRAINT IF EXISTS fk_effective_unavailability_planned_unavailability_id;
DROP INDEX IF EXISTS ix_effective_unavailability_planned_unavailability_id;

ALTER TABLE IF EXISTS parent_criteria
    DROP CONSTRAINT IF EXISTS fk_parent_criteria_parent_id;

ALTER TABLE IF EXISTS parent
    DROP CONSTRAINT IF EXISTS fk_parent_account_id;

ALTER TABLE IF EXISTS parent
    DROP CONSTRAINT IF EXISTS fk_parent_sponsor_id;
DROP INDEX IF EXISTS ix_parent_sponsor_id;

ALTER TABLE IF EXISTS parent_option
    DROP CONSTRAINT IF EXISTS fk_parent_option_parent_id;
DROP INDEX IF EXISTS ix_parent_option_parent_id;

ALTER TABLE IF EXISTS planned_unavailability
    DROP CONSTRAINT IF EXISTS fk_planned_unavailability_sitter_id;
DROP INDEX IF EXISTS ix_planned_unavailability_sitter_id;

ALTER TABLE IF EXISTS role
    DROP CONSTRAINT IF EXISTS fk_role_account_id;
DROP INDEX IF EXISTS ix_role_account_id;

ALTER TABLE IF EXISTS sitter_attribute
    DROP CONSTRAINT IF EXISTS fk_sitter_attribute_sitter_id;

ALTER TABLE IF EXISTS sitter
    DROP CONSTRAINT IF EXISTS fk_sitter_account_id;

ALTER TABLE IF EXISTS sitter_option
    DROP CONSTRAINT IF EXISTS fk_sitter_option_sitter_id;
DROP INDEX IF EXISTS ix_sitter_option_sitter_id;

DROP TABLE IF EXISTS account CASCADE;

DROP TABLE IF EXISTS booked_care CASCADE;

DROP TABLE IF EXISTS booked_care_children CASCADE;

DROP TABLE IF EXISTS caf_option CASCADE;

DROP TABLE IF EXISTS care_criteria CASCADE;

DROP TABLE IF EXISTS children CASCADE;

DROP TABLE IF EXISTS children_has_booked_care CASCADE;

DROP TABLE IF EXISTS effective_care CASCADE;

DROP TABLE IF EXISTS effective_unavailability CASCADE;

DROP TABLE IF EXISTS parent_criteria CASCADE;

DROP TABLE IF EXISTS parent CASCADE;

DROP TABLE IF EXISTS parent_option CASCADE;

DROP TABLE IF EXISTS planned_unavailability CASCADE;

DROP TABLE IF EXISTS role CASCADE;

DROP TABLE IF EXISTS sitter_attribute CASCADE;

DROP TABLE IF EXISTS sitter CASCADE;

DROP TABLE IF EXISTS sitter_option CASCADE;
