CREATE TABLE IF NOT EXISTS save_points (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider TEXT NOT NULL,
    provider_subject TEXT NOT NULL,
    action VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_save_points_provider_subject_created
    ON save_points (provider, provider_subject, created_at DESC);