package com.ragassistant.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Metadata record for a chunk of an ingested document.
 *
 * NOTE: The actual embedding vector is stored and managed by Spring AI's
 * VectorStore (in its own table, configured via application.yml). This entity
 * is a separate, human-readable index over the same chunks -- useful for
 * admin views, re-ingestion, and auditing without querying the vector table directly.
 */
@Entity
@Table(name = "document_chunk")
public class DocumentChunk {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
