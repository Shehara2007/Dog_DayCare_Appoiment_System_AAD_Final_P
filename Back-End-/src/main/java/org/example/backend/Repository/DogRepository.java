package org.example.backend.Repository;

import org.example.backend.Entity.dogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogRepository extends JpaRepository<dogEntity, Integer> {
}