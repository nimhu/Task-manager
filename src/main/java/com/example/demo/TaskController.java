package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repo;

    @Autowired
    private AIService aiService;

    @GetMapping
    public List<Task> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Task create(@Valid @RequestBody Task task) {
        return repo.save(task);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    @GetMapping("/filter")
    public List<Task> getByStatus(@RequestParam String status) {
        return repo.findByStatus(status);
    }

    @PutMapping("/{id}")
    public Task updateStatus(@PathVariable Long id, @RequestParam String status) {
        Task task = repo.findById(id).orElseThrow();
        task.setStatus(status);
        return repo.save(task);
    }

    @PostMapping("/ai")
    public Task createWithAI(@RequestBody Task task) {
        String status = aiService.suggestStatus(task.getDescription());
        task.setStatus(status);
        return repo.save(task);
    }
}