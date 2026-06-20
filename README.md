# RAG-Based-Document-Q-A-Assistant
AI-powered knowledge assistant that answers questions from your own documents using Retrieval-Augmented Generation, Spring Boot, and Claude.
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
