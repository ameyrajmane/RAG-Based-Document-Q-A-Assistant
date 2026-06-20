# RAG Assistant

An AI-powered document Q&A backend: upload documents, ask questions in plain
English, get answers grounded in your own data with cited sources.

## Stack

- **Java 21 + Spring Boot 3.3** — REST API
- **Spring AI** — orchestration layer over the LLM and embedding calls
- **Claude (Anthropic API)** — answer generation
- **OpenAI embeddings** — used for vectorization (Anthropic doesn't expose an embeddings API)
- **PostgreSQL + pgvector** — vector similarity search
- **Redis** — caches repeated question/answer pairs to cut latency and API cost
- **Docker / Jenkins / Azure** — same deployment pattern as a typical production Spring Boot service

## Architecture

```
 Upload ──▶ Chunk ──▶ Embed (OpenAI) ──▶ Store in pgvector
                                              │
 Question ──▶ Embed ──▶ Similarity search ───┘
                              │
                         Top-k chunks
                              │
                    Prompt + Claude (Anthropic)
                              │
                      Answer + sources
                              │
                      Cached in Redis
```

## Local setup

1. Copy `.env.example` to `.env` and fill in your API keys:
   ```
   ANTHROPIC_API_KEY=sk-ant-...
   OPENAI_API_KEY=sk-...
   ```
2. Start everything:
   ```
   docker compose up --build
   ```
3. Upload a document:
   ```
   curl -F "file=@notes.txt" http://localhost:8080/api/documents/upload
   ```
4. Ask a question:
   ```
   curl -X POST http://localhost:8080/api/chat \
     -H "Content-Type: application/json" \
     -d '{"question": "What does this document say about X?"}'
   ```

## What's intentionally left for you to build

This scaffold gets the core RAG loop working end to end, but to make it
resume-ready you should add:

- **Real file parsing** — swap the raw-text read in `DocumentIngestionService`
  for Spring AI's `TikaDocumentReader` to handle PDF/DOCX/HTML uploads.
- **Auth** — JWT-based auth on the API (you already know Spring Security).
- **Latency benchmarks** — measure cache hit/miss response times; this becomes
  your resume metric ("reduced average query latency by X%").
- **Frontend** — a minimal React chat UI calling `/api/chat`.
- **Error handling** — retries/fallback if the LLM call fails or times out.

## Notes on versions

Spring AI is moving fast — check
[docs.spring.io/spring-ai](https://docs.spring.io/spring-ai/reference/) for
the current GA version and exact starter artifact names before you build;
the `pom.xml` here uses a milestone version as a placeholder.
