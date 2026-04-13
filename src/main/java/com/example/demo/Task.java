package com.example.demo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


@Entity
    public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "Titlul este obligatoriu")
        private String title;
        private String description;

        @NotBlank(message = "Status-ul este obligatoriu")
        @Pattern(regexp = "TODO|IN_PROGRESS|DONE", message = "Status invalid")
        private String status; // "TODO", "IN_PROGRESS", "DONE"

        // getters + setters

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

