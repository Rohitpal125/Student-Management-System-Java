package main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Websites that are allowed to call this backend.
    // Change with the ALLOWED_ORIGINS environment variable (comma separated).
    private static final String DEFAULT_ORIGINS =
            "http://localhost:63342,http://localhost:5500,http://127.0.0.1:5500,"
                    + "https://rohitpal125.github.io";

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        String origins = System.getenv("ALLOWED_ORIGINS");
        if (origins == null || origins.isBlank()) {
            origins = DEFAULT_ORIGINS;
        }

        registry.addMapping("/api/**")
                .allowedOrigins(origins.split("\\s*,\\s*"))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/students", "/api/students/**");
    }
}