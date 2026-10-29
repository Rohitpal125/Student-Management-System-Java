package service;

import exception.AiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import util.AiHelper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    private final StudentService studentService;
    private final RestClient client;

    public AiService(StudentService studentService) {
        this.studentService = studentService;

        // Do not wait forever: 5 seconds to connect, 30 seconds for the answer
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(30));

        this.client = RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(factory)
                .build();
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    // Builds a short report about the students using the Gemini API
    public String generateInsights() {

        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiException(503, "AI is not set up on the server yet.");
        }

        String model = env("GEMINI_MODEL", "gemini-3.5-flash");

        // Only numbers go to the AI. No names, emails or phone numbers.
        String stats = AiHelper.buildStats(studentService.getAllStudents());
        if (stats.isEmpty()) {
            throw new AiException(400, "Add some students first.");
        }

        // JSON body:  { "contents": [ { "parts": [ { "text": "..." } ] } ] }
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", AiHelper.buildPrompt(stats))))));

        Map<String, Object> response;

        try {
            response = client.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() { });

        } catch (RestClientResponseException e) {
            int code = e.getStatusCode().value();
            System.out.println("Gemini error " + code + ": " + e.getResponseBodyAsString());

            if (code == 429) {
                throw new AiException(429, "The free AI limit is reached. Please try again in a minute.");
            }
            if (code == 404) {
                throw new AiException(502, "The AI model name looks wrong. Check GEMINI_MODEL.");
            }
            if (code == 400 || code == 403) {
                throw new AiException(502, "The AI key or request was rejected. Check GEMINI_API_KEY and GEMINI_MODEL.");
            }
            throw new AiException(502, "The AI service returned an error.");

        } catch (ResourceAccessException e) {
            throw new AiException(504, "The AI service did not answer in time. Please try again.");
        }

        String text = AiHelper.extractText(response);
        if (text == null) {
            throw new AiException(502, "The AI did not return any text. Please try again.");
        }

        return text;
    }
}