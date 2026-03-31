package org.example.backend.service;

import org.example.backend.Entity.Dog;
import org.example.backend.dto.CreateDogRequest;
import org.example.backend.dto.UpdateDogRequest;

import java.util.List;

public interface DogService {
    Dog register(CreateDogRequest request);
    Dog getById(Long id);
    Dog getByQrAccessToken(String qrAccessToken);
    List<Dog> getByOwner(Long ownerId);
    List<Dog> getAll();
    Dog update(Long id, UpdateDogRequest request);
    void delete(Long id);
}
