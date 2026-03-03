package org.example.backend.Repository;

import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByOwner(User owner);
    List<Dog> findByOwnerId(Long ownerId);
}