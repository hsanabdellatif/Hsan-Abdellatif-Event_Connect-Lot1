package com.eventconnect.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur de test pour vérifier l'API
 */
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "EventConnect API is running");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-public")
    public ResponseEntity<Map<String, String>> testPublic() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint public accessible");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-protected")
    public ResponseEntity<Map<String, String>> testProtected() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint protégé - JWT valide requis");
        return ResponseEntity.ok(response);
    }
}
