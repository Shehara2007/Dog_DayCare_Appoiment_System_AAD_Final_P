package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class DogDTOs {

    @Data
    public static class DogRequest {
        @NotBlank
        private String name;
        private String breed;
        private Integer age;
        private String gender;
        private Double weight;
    }

    @Data
    public static class DogResponse {
        private Long id;
        private String name;
        private String breed;
        private Integer age;
        private String gender;
        private Double weight;
        private String profileImageUrl;
        private String qrCodePath;
        private Long ownerId;
        private String ownerName;
        private LocalDateTime createdAt;
    }
}