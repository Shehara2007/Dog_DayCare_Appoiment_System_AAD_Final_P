package org.example.backend.Repository;

import org.example.backend.Entity.DaycareService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaycareServiceRepository extends JpaRepository<DaycareService, Integer> {
}