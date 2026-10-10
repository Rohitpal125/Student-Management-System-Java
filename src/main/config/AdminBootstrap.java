package main.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.AuthService;

// Runs once when the app starts. Creates the admin from environment variables.
@Component
public class AdminBootstrap implements CommandLineRunner {

    private final AuthService authService;

    public AdminBootstrap(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {

        String username = System.getenv("ADMIN_USERNAME");
        String password = System.getenv("ADMIN_PASSWORD");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            System.out.println("ADMIN_USERNAME / ADMIN_PASSWORD not set. No admin created.");
            return;
        }

        if (password.length() < 8) {
            System.out.println("ADMIN_PASSWORD must be at least 8 characters. No admin created.");
            return;
        }

        boolean ok = authService.createOrUpdateAdmin(username.trim(), password);

        System.out.println(ok
                ? "Admin account ready: " + username.trim()
                : "Could not create the admin account. Check the database connection.");
    }
}