CREATE TABLE emergencies (
    id UUID NOT NULL,
    type VARCHAR(255) NOT NULL,
    priority VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    latitude NUMERIC(38, 2),
    longitude NUMERIC(38, 2),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_emergencies PRIMARY KEY (id)
);

CREATE TABLE outbox_events (
    id UUID NOT NULL,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_version INTEGER NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_outbox_events PRIMARY KEY (id)
);

CREATE TABLE processed_events (
    id UUID NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT pk_processed_events PRIMARY KEY (id)
);