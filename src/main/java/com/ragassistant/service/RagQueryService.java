package com.ragassistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagQueryService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagQueryService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Cached on the question text -- repeated questions skip both the vector
     * search and the LLM call entirely. TTL is configured in application.yml.
     */
    @Cacheable(value = "ragAnswers", key = "#question")
    public RagAnswer answer(String question) {
        List<Document> relevant = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(5)
        );

        String context = relevant.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n---\n"));

        String prompt = """
                Answer the question using ONLY the context below.
                If the answer isn't in the context, say you don't have enough information.

                Context:
                %s

                Question: %s
                """.formatted(context, question);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        List<String> sources = relevant.stream()
                .map(d -> String.valueOf(d.getMetadata().get("source")))
                .distinct()
                .toList();

        return new RagAnswer(response, sources);
    }

    public record RagAnswer(String answer, List<String> sources) {}
}
