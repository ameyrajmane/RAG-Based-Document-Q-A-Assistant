package com.ragassistant.service;

import com.ragassistant.model.DocumentChunk;
import com.ragassistant.repository.DocumentChunkRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    private final DocumentChunkRepository chunkRepository;
    private final TokenTextSplitter splitter = new TokenTextSplitter();

    public DocumentIngestionService(VectorStore vectorStore, DocumentChunkRepository chunkRepository) {
        this.vectorStore = vectorStore;
        this.chunkRepository = chunkRepository;
    }

    /**
     * Splits the uploaded file into chunks, embeds them, and persists both
     * the vectors (via VectorStore) and the readable metadata (via JPA).
     *
     * For real PDFs/DOCX you'd swap the raw text read for Spring AI's
     * TikaDocumentReader, which handles PDF/Word/HTML extraction for you.
     */
    public int ingest(MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);

        Document rawDoc = new Document(text, Map.of("source", file.getOriginalFilename()));
        List<Document> chunks = splitter.apply(List.of(rawDoc));

        // Embeds each chunk (via the configured OpenAI embedding model) and
        // writes it to the pgvector table managed by Spring AI.
        vectorStore.add(chunks);

        int index = 0;
        for (Document chunk : chunks) {
            DocumentChunk record = new DocumentChunk();
            record.setDocumentName(file.getOriginalFilename());
            record.setChunkIndex(index++);
            record.setContent(chunk.getContent());
            record.setCreatedAt(Instant.now());
            chunkRepository.save(record);
        }
        return chunks.size();
    }
}
