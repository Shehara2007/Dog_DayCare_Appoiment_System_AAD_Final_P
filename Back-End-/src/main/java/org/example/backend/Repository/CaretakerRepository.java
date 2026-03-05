package org.example.backend.Repository;

import org.example.backend.Entity.Caretaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaretakerRepository extends JpaRepository<Caretaker, Integer> {
}