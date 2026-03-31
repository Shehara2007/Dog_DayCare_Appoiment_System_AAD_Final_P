package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.DogRepository;
import org.example.backend.dto.CreateDogRequest;
import org.example.backend.dto.UpdateDogRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.exeception.ResourceNotFoundException;
import org.example.backend.service.DogService;
import org.example.backend.service.NotificationService;
import org.example.backend.service.QrCodeService;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final UserService userService;
    private final QrCodeService qrCodeService;
    private final NotificationService notificationService;

    @Value("${app.qr.scan-base-url:http://localhost:8080/api/v1/health-reports/qr-ui}")
    private String qrScanBaseUrl;

    @Override
    @Transactional
    public Dog register(CreateDogRequest request) {
        User owner = userService.getById(request.getOwnerId());
        if (owner.getRole() != UserRole.PET_OWNER) {
            throw new BusinessException("Only PET_OWNER users can own dogs");
        }

        Dog dog = new Dog();
        dog.setName(request.getName());
        dog.setBreed(request.getBreed());
        dog.setDateOfBirth(request.getDateOfBirth());
        dog.setOwner(owner);
        dog.setQrAccessToken(UUID.randomUUID().toString());
        dog.setQrIssuedAt(LocalDateTime.now());
        Dog savedDog = dogRepository.save(dog);

        String qrScanUrl = buildQrScanUrl(savedDog.getQrAccessToken());
        savedDog.setQrCodeBase64(qrCodeService.generateBase64Qr(qrScanUrl));
        savedDog = dogRepository.save(savedDog);

        notificationService.sendDogRegistrationQr(owner, savedDog, qrScanUrl);
        savedDog.setQrEmailedAt(LocalDateTime.now());
        return dogRepository.save(savedDog);
    }

    @Override
    public Dog getById(Long id) {
        return dogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found: " + id));
    }

    @Override
    public Dog getByQrAccessToken(String qrAccessToken) {
        return dogRepository.findByQrAccessToken(qrAccessToken)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found for provided QR token"));
    }

    @Override
    public List<Dog> getByOwner(Long ownerId) {
        return dogRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Dog> getAll() {
        return dogRepository.findAll();
    }

    @Override
    @Transactional
    public Dog update(Long id, UpdateDogRequest request) {
        Dog dog = getById(id);
        dog.setName(request.getName());
        dog.setBreed(request.getBreed());
        dog.setDateOfBirth(request.getDateOfBirth());
        return dogRepository.save(dog);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Dog dog = getById(id);
        dogRepository.delete(dog);
    }

    private String buildQrScanUrl(String qrAccessToken) {
        String normalizedBaseUrl = StringUtils.trimTrailingCharacter(qrScanBaseUrl.trim(), '/');
        return normalizedBaseUrl + "/" + qrAccessToken;
    }
}

