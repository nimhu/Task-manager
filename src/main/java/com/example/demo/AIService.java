package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class AIService {

    public String suggestStatus(String description) {
        if (description == null) return "TODO";

        String lower = description.toLowerCase();

        if (lower.contains("terminat") || lower.contains("gata") ||
                lower.contains("finalizat") || lower.contains("done") ||
                lower.contains("complet") || lower.contains("finished")) {
            return "DONE";
        } else if (lower.contains("lucrez") || lower.contains("progres") ||
                lower.contains("implementez") || lower.contains("in progress") ||
                lower.contains("lucrand") || lower.contains("started")) {
            return "IN_PROGRESS";
        } else {
            return "TODO";
        }
    }
}