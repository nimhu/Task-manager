package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repo;

    @Autowired
    private AIService aiService;

    @GetMapping
    public List<Task> getAll(Authentication auth) {
        return repo.findByUsername(auth.getName());
    }

    @PostMapping
    public Task create(@Valid @RequestBody Task task, Authentication auth) {
        task.setUsername(auth.getName());
        return repo.save(task);
    }

    @PostMapping("/ai")
    public Task createWithAI(@RequestBody Task task, Authentication auth) {
        String status = aiService.suggestStatus(task.getDescription());
        task.setStatus(status);
        task.setUsername(auth.getName());
        return repo.save(task);
    }

    @PutMapping("/{id}")
    public Task updateStatus(@PathVariable Long id,
                             @RequestParam String status,
                             Authentication auth) {
        Task task = repo.findById(id).orElseThrow();
        if (!task.getUsername().equals(auth.getName())) {
            throw new RuntimeException("Nu ai acces la acest task!");
        }
        task.setStatus(status);
        return repo.save(task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        Task task = repo.findById(id).orElseThrow();
        if (!task.getUsername().equals(auth.getName())) {
            throw new RuntimeException("Nu ai acces la acest task!");
        }
        repo.deleteById(id);
    }

    @GetMapping("/filter")
    public List<Task> getByStatus(@RequestParam String status, Authentication auth) {
        return repo.findByUsernameAndStatus(auth.getName(), status);
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request, Authentication auth) {
        String message = request.get("message");
        List<Task> tasks = repo.findByUsername(auth.getName());
        String response = aiService.chat(message, tasks);
        return Map.of("response", response);
    }

    @PostMapping("/chat/update")
    public Map<String, String> chatUpdate(@RequestBody Map<String, String> request, Authentication auth) {
        Long id = Long.parseLong(request.get("id"));
        String status = request.get("status");
        Task task = repo.findById(id).orElseThrow();
        if (task.getUsername().equals(auth.getName())) {
            task.setStatus(status);
            repo.save(task);
        }
        return Map.of("result", "ok");
    }
}