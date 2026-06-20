-- Enable pgvector extension (one-time, per database)
CREATE EXTENSION IF NOT EXISTS vector;

-- Human-readable metadata table (separate from Spring AI's own vector_store table,
-- which is auto-created on first run based on the dimensions in application.yml)
CREATE TABLE IF NOT EXISTS document_chunk (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_name VARCHAR(255) NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX IF NOT EXISTS document_chunk_name_idx
    ON document_chunk (document_name);
