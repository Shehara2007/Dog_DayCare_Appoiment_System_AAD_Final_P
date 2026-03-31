package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Notification;
import org.example.backend.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/owner/{ownerId}")
    public List<Map<String, Object>> byOwner(@PathVariable Long ownerId) {
        return notificationService.getByOwner(ownerId).stream().map(this::mapNotification).toList();
    }

    private Map<String, Object> mapNotification(Notification notification) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", notification.getId());
        response.put("ownerId", notification.getOwner().getId());
        response.put("dogId", notification.getDog() == null ? null : notification.getDog().getId());
        response.put("type", notification.getType());
        response.put("message", notification.getMessage());
        response.put("readFlag", notification.isReadFlag());
        response.put("createdAt", notification.getCreatedAt());
        return response;
    }
}


