package com.ragassistant.controller;

import com.ragassistant.service.DocumentIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    public DocumentController(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        int chunks = ingestionService.ingest(file);
        return ResponseEntity.ok(Map.of(
                "fileName", file.getOriginalFilename(),
                "chunksIndexed", chunks
        ));
    }
}
