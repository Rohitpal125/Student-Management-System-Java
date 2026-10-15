package main.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.AuthService;

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

        String name = username.trim();
        boolean reset = "true".equalsIgnoreCase(System.getenv("RESET_ADMIN_PASSWORD"));

        if (authService.adminExists(name) && !reset) {
            System.out.println("Admin already exists: " + name + " (password not changed)");
            return;
        }

        boolean ok = authService.createOrUpdateAdmin(name, password);

        System.out.println(ok
                ? "Admin account ready: " + name
                : "Could not create the admin account. Check the database connection.");
    }
}