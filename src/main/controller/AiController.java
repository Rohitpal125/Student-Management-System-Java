package main.controller;

import exception.AiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AiService;

import java.util.Map;

@RestController
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // POST, so AuthInterceptor makes it admin-only (it checks every non-GET request)
    @PostMapping("/api/ai/insights")
    public ResponseEntity<Map<String, String>> insights() {
        try {
            return ResponseEntity.ok(Map.of("insight", aiService.generateInsights()));

        } catch (AiException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of("message", e.getMessage()));
        }
    }
}