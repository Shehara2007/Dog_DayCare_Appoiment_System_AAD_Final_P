package org.example.backend.Repository;

import org.example.backend.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}

