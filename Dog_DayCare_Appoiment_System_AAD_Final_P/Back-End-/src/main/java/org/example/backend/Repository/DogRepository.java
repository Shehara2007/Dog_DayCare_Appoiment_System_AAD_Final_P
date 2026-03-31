package org.example.backend.Repository;

import org.example.backend.Entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {
    List<Dog> findByOwnerId(Long ownerId);
    Optional<Dog> findByQrAccessToken(String qrAccessToken);
}

