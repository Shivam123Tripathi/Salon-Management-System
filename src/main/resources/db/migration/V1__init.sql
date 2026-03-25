-- =============================================================
-- V1__init.sql — INITIAL DATABASE SCHEMA
-- =============================================================
--
-- WHAT IS THIS FILE?
-- This is a Flyway migration script. Flyway runs it ONCE against your database.
-- After running, Flyway records it in a "flyway_schema_history" table
-- so it NEVER runs the same script twice.
--
-- NAMING CONVENTION: V{version}__{description}.sql
--   V1  = Version 1 (must be unique, runs in order)
--   __  = Double underscore (required separator)
--   init = Human-readable description
--
-- TO ADD CHANGES LATER:
--   Create V2__add_column.sql, V3__create_new_table.sql, etc.
--   NEVER modify V1__init.sql after it has been applied!
-- =============================================================

-- -----------------------------------------------------------
-- TABLE: users
-- -----------------------------------------------------------
-- Stores all user accounts: customers, admins, and artists.
-- The 'role' column determines what they can do in the system.
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    full_name       VARCHAR(255)    NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL      COMMENT 'BCrypt hashed, NEVER plain text',
    phone           VARCHAR(20)     NULL,
    role            VARCHAR(20)     NOT NULL      COMMENT 'CUSTOMER, ADMIN, or ARTIST',
    address         VARCHAR(500)    NULL,
    created_at      DATETIME        NULL          DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: artists
-- -----------------------------------------------------------
-- Extends a User with salon-specific fields.
-- Each artist has EXACTLY ONE user account (for login).
CREATE TABLE IF NOT EXISTS artists (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT          NOT NULL,
    specialization          VARCHAR(255)    NULL      COMMENT 'e.g., Hair Stylist, Nail Tech',
    experience_years        INT             NULL,
    rating                  DOUBLE          NULL      DEFAULT 0.0,
    total_reviews           INT             NULL      DEFAULT 0,
    availability_status     VARCHAR(20)     NOT NULL  DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE, BUSY, ON_LEAVE',
    working_hours_start     TIME            NULL      COMMENT 'Shift start e.g. 09:00',
    working_hours_end       TIME            NULL      COMMENT 'Shift end e.g. 18:00',
    break_start_time        TIME            NULL      COMMENT 'Lunch break start',
    break_end_time          TIME            NULL      COMMENT 'Lunch break end',

    PRIMARY KEY (id),
    UNIQUE KEY uk_artists_user (user_id),
    CONSTRAINT fk_artists_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: services
-- -----------------------------------------------------------
-- Salon service catalog (Haircut, Facial, Manicure, etc.)
-- Uses DECIMAL(10,2) for price — never use FLOAT/DOUBLE for money!
CREATE TABLE IF NOT EXISTS services (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    name                VARCHAR(255)    NOT NULL,
    description         VARCHAR(1000)   NULL,
    duration_minutes    INT             NOT NULL      COMMENT 'How long the service takes',
    price               DECIMAL(10,2)   NOT NULL      COMMENT 'Cost in your currency',
    category            VARCHAR(100)    NULL          COMMENT 'Hair, Skin, Nails, etc.',
    active              TINYINT(1)      NOT NULL      DEFAULT 1 COMMENT '1=active, 0=soft-deleted',

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: artist_service_mappings
-- -----------------------------------------------------------
-- Many-to-Many: which artist can perform which services.
-- One artist can do many services; one service can be done by many artists.
CREATE TABLE IF NOT EXISTS artist_service_mappings (
    id          BIGINT  NOT NULL AUTO_INCREMENT,
    artist_id   BIGINT  NOT NULL,
    service_id  BIGINT  NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_artist_service (artist_id, service_id),
    CONSTRAINT fk_asm_artist  FOREIGN KEY (artist_id)  REFERENCES artists(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_asm_service FOREIGN KEY (service_id) REFERENCES services(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: appointments
-- -----------------------------------------------------------
-- Core booking table. Links a customer → artist → service at a date/time.
-- The DOUBLE-BOOKING check is done in Java code (AppointmentRepository query),
-- but we also add an index for fast overlap queries.
CREATE TABLE IF NOT EXISTS appointments (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    customer_id         BIGINT          NOT NULL,
    artist_id           BIGINT          NOT NULL,
    service_id          BIGINT          NOT NULL,
    appointment_date    DATE            NOT NULL,
    start_time          TIME            NOT NULL,
    end_time            TIME            NOT NULL,
    status              VARCHAR(20)     NOT NULL  DEFAULT 'BOOKED' COMMENT 'BOOKED, COMPLETED, CANCELLED, NO_SHOW',
    notes               VARCHAR(500)    NULL,
    created_at          DATETIME        NULL      DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NULL      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_appt_customer FOREIGN KEY (customer_id) REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_appt_artist   FOREIGN KEY (artist_id)   REFERENCES artists(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_appt_service  FOREIGN KEY (service_id)  REFERENCES services(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Index for the double-booking overlap query
    -- (SELECT ... WHERE artist_id=? AND appointment_date=? AND start_time < ? AND end_time > ?)
    INDEX idx_appt_artist_date (artist_id, appointment_date, start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: payments
-- -----------------------------------------------------------
-- Tracks payment for each appointment. One appointment = one payment.
CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    appointment_id  BIGINT          NOT NULL,
    amount          DECIMAL(10,2)   NOT NULL,
    method          VARCHAR(20)     NOT NULL  COMMENT 'UPI, CARD, PAY_AT_SALON',
    status          VARCHAR(20)     NOT NULL  DEFAULT 'PENDING' COMMENT 'PENDING, COMPLETED, REFUNDED',
    transaction_id  VARCHAR(255)    NULL      COMMENT 'External gateway reference',
    paid_at         DATETIME        NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_payments_appointment (appointment_id),
    CONSTRAINT fk_payments_appt FOREIGN KEY (appointment_id) REFERENCES appointments(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: reviews
-- -----------------------------------------------------------
-- Customer reviews tied to specific appointments.
-- One review per appointment (enforced by unique constraint).
CREATE TABLE IF NOT EXISTS reviews (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    customer_id     BIGINT          NOT NULL,
    artist_id       BIGINT          NOT NULL,
    service_id      BIGINT          NOT NULL,
    appointment_id  BIGINT          NOT NULL,
    rating          INT             NOT NULL  COMMENT '1 to 5 stars',
    comment         VARCHAR(1000)   NULL,
    created_at      DATETIME        NULL      DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_reviews_appointment (appointment_id),
    CONSTRAINT fk_reviews_customer FOREIGN KEY (customer_id)    REFERENCES users(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reviews_artist   FOREIGN KEY (artist_id)      REFERENCES artists(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reviews_service  FOREIGN KEY (service_id)     REFERENCES services(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_reviews_appt     FOREIGN KEY (appointment_id) REFERENCES appointments(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: salon_config
-- -----------------------------------------------------------
-- Singleton table: only ONE row. Stores global salon settings.
CREATE TABLE IF NOT EXISTS salon_config (
    id                              BIGINT          NOT NULL AUTO_INCREMENT,
    salon_name                      VARCHAR(255)    NOT NULL,
    opening_time                    TIME            NOT NULL,
    closing_time                    TIME            NOT NULL,
    cancellation_policy_hours       INT             NOT NULL  DEFAULT 2,
    default_slot_interval_minutes   INT             NOT NULL  DEFAULT 30,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- TABLE: artist_leaves
-- -----------------------------------------------------------
-- Tracks planned artist absences (vacation, sick days).
CREATE TABLE IF NOT EXISTS artist_leaves (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    artist_id   BIGINT      NOT NULL,
    leave_date  DATE        NOT NULL,
    reason      VARCHAR(255) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_artist_leave_date (artist_id, leave_date),
    CONSTRAINT fk_leaves_artist FOREIGN KEY (artist_id) REFERENCES artists(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- -----------------------------------------------------------
-- SEED DATA: Default salon configuration
-- -----------------------------------------------------------
-- Insert default config so the app works immediately.
-- The DataInitializer.java also does this, but having it in SQL
-- makes it work even in production (where Flyway runs, not DataInitializer).
INSERT INTO salon_config (salon_name, opening_time, closing_time, cancellation_policy_hours, default_slot_interval_minutes)
VALUES ('My Salon', '09:00:00', '21:00:00', 2, 30);
