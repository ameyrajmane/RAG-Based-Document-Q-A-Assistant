package com.ragassistant.controller;

import com.ragassistant.dto.ChatRequest;
import com.ragassistant.service.RagQueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagQueryService ragQueryService;

    public ChatController(RagQueryService ragQueryService) {
        this.ragQueryService = ragQueryService;
    }

    @PostMapping
    public RagQueryService.RagAnswer chat(@RequestBody ChatRequest request) {
        return ragQueryService.answer(request.question());
    }
}
