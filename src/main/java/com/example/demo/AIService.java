package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final String API_KEY = "AIzaSyDEpDnwfJhGcXpVwZu-bkR9UIeXcgGZim0";

    private final WebClient client = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public String suggestStatus(String description) {
        try {
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

        } catch (Exception e) {
            return suggestStatusLocal(description);
        }
    }

    public String chat(String message, List<Task> tasks) {
        try {
            StringBuilder taskList = new StringBuilder();
            for (Task t : tasks) {
                taskList.append("- ID: ").append(t.getId())
                        .append(", Titlu: ").append(t.getTitle())
                        .append(", Status: ").append(t.getStatus())
                        .append("\n");
            }

            String prompt = "Esti un asistent pentru managementul taskurilor. " +
                    "Taskurile utilizatorului sunt:\n" + taskList +
                    "\nMesajul utilizatorului: '" + message + "'\n" +
                    "Raspunde in romana. Daca utilizatorul a terminat un task, spune-i explicit: " +
                    "UPDATE_TASK:[id]:[STATUS] (ex: UPDATE_TASK:1:DONE). " +
                    "Altfel raspunde normal la intrebare.";

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
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
            return parts.get(0).get("text").toString().trim();

        } catch (Exception e) {
            return "Asistentul AI nu este disponibil momentan. Incearca din nou mai tarziu.";
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