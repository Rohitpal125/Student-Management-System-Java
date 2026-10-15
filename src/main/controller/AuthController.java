package main.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.AuthService;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {

        String username = body.get("username");
        String token = authService.login(username, body.get("password"));

        if (token == null) {
            // Small pause makes password guessing slower
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Wrong username or password"));
        }

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", username.trim()
        ));
    }

    // Only a logged-in admin can reach this (AuthInterceptor checks the token)
    @PostMapping("/api/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> body,
                                                              HttpServletRequest request) {

        String username = (String) request.getAttribute("adminUsername");

        if (username == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Admin login required"));
        }

        String error = authService.changePassword(
                username, body.get("oldPassword"), body.get("newPassword"));

        if (error != null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", error));
        }

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}