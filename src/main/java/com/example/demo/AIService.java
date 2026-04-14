package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.List;

@Service
public class AIService {

    private final String API_KEY = "AIzaSyDb-EAupYE6S-XBw6Wm-nyTm6_aeEqD4QA";

    private final WebClient client = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public String suggestStatus(String description) {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text",
                                        "Bazat pe aceasta descriere de task: '" + description +
                                                "', raspunde DOAR cu unul din aceste cuvinte: TODO, IN_PROGRESS, sau DONE. Nimic altceva.")
                        ))
                )
        );

        var response = client.post()
                .uri("/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        var candidates = (List<Map<String, Object>>) response.get("candidates");
        var content = (Map<String, Object>) candidates.get(0).get("content");
        var parts = (List<Map<String, Object>>) content.get("parts");

        String result = parts.get(0).get("text").toString().trim().toUpperCase();

        if (result.contains("DONE")) return "DONE";
        if (result.contains("IN_PROGRESS")) return "IN_PROGRESS";
        return "TODO";
    }
}