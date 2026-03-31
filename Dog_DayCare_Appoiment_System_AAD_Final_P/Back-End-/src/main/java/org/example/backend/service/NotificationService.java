package org.example.backend.service;

import org.example.backend.Entity.Dog;
import org.example.backend.Entity.Notification;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.NotificationType;

import java.util.List;

public interface NotificationService {
    Notification create(User owner, Dog dog, NotificationType type, String message);
    void sendDogRegistrationQr(User owner, Dog dog, String qrScanUrl);
    List<Notification> getByOwner(Long ownerId);
}
