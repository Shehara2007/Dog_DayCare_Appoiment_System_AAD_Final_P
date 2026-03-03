package org.example.backend.Repository;

import org.example.backend.Entity.QRCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findByDogId(Long dogId);
}