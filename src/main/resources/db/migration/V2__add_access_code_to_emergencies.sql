ALTER TABLE emergencies
ADD COLUMN access_code VARCHAR(8) NOT NULL DEFAULT '';

CREATE INDEX idx_emergencies_access_code ON emergencies (access_code);