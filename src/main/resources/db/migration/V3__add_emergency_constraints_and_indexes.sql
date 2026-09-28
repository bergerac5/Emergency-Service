ALTER TABLE emergencies
    ADD CONSTRAINT chk_emergencies_type
        CHECK (type IN ('FIRE', 'MEDICAL', 'POLICE', 'NATURAL_DISASTER', 'OTHER')),
    ADD CONSTRAINT chk_emergencies_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    ADD CONSTRAINT chk_emergencies_status
        CHECK (status IN ('REPORTED', 'WAITING_FOR_DISPATCH', 'DISPATCHING', 'DISPATCHED',
                          'AMBULANCE_EN_ROUTE', 'ON_SCENE', 'COMPLETED', 'CANCELLED', 'FAILED')),
    ADD CONSTRAINT chk_emergencies_latitude
        CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
    ADD CONSTRAINT chk_emergencies_longitude
        CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180);

CREATE INDEX idx_emergencies_status_created_at
    ON emergencies (status, created_at DESC);