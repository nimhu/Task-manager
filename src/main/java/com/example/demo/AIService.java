package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final String API_KEY = System.getenv("OPENAI_API_KEY");

    private final WebClient client = WebClient.builder()
            .baseUrl("https://api.openai.com")
            .defaultHeader("Authorization", "Bearer " + API_KEY)
            .build();


    public String suggestStatus(String description) {
        try {
            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "max_tokens", 10,
                    "messages", List.of(
                            Map.of("role", "user", "content",
                                    "Bazat pe aceasta descriere de task: '" + description +
                                            "', raspunde DOAR cu unul din aceste cuvinte: TODO, IN_PROGRESS, sau DONE. Nimic altceva.")
                    )
            );

            var response = client.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            var choices = (List<Map<String, Object>>) response.get("choices");
            var message = (Map<String, Object>) choices.get(0).get("message");
            String result = message.get("content").toString().trim().toUpperCase();

            if (result.contains("DONE")) return "DONE";
            if (result.contains("IN_PROGRESS")) return "IN_PROGRESS";
            return "TODO";

        } catch (Exception e) {
            return suggestStatusLocal(description);
        }
    }

    public String chat(String message, List<Task> tasks) {
        try {
            StringBuilder taskList = new StringBuilder();
            for (Task t : tasks) {
                taskList.append(t.getId()).append(":").append(t.getTitle())
                        .append(":").append(t.getStatus()).append("\n");
            }

            String prompt = "Taskuri:\n" + taskList +
                    "\nMesaj: '" + message + "'\n" +
                    "Raspunde scurt in romana. Daca un task e terminat scrie: UPDATE_TASK:[id]:DONE. " +
                    "Daca e in progres: UPDATE_TASK:[id]:IN_PROGRESS.";

            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "max_tokens", 200,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            var response = client.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            var choices = (List<Map<String, Object>>) response.get("choices");
            var msg = (Map<String, Object>) choices.get(0).get("message");
            return msg.get("content").toString().trim();

        } catch (Exception e) {
            return "Eroare: " + e.getMessage();
        }
    }

    private String suggestStatusLocal(String description) {
        if (description == null) return "TODO";
        String lower = description.toLowerCase();
        if (lower.contains("terminat") || lower.contains("gata") ||
                lower.contains("finalizat") || lower.contains("done") ||
                lower.contains("complet") || lower.contains("finished")) {
            return "DONE";
        } else if (lower.contains("lucrez") || lower.contains("progres") ||
                lower.contains("implementez") || lower.contains("in progress") ||
                lower.contains("started")) {
            return "IN_PROGRESS";
        }
        return "TODO";
    }
}