package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.QRCodeRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.QRCode;
import org.example.backend.Entity.User;
import org.example.backend.dto.DogDTOs;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final QRCodeRepository qrCodeRepository;
    private final QRCodeService qrCodeService;

    @Transactional
    public DogDTOs.DogResponse registerDog(DogDTOs.DogRequest request) {
        User currentUser = getCurrentUser();

        Dog dog = Dog.builder()
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .weight(request.getWeight())
                .owner(currentUser)
                .build();

        dog = dogRepository.save(dog);

        // Generate QR Code
        try {
            String baseUrl = "http://localhost:8080";
            String qrPath = qrCodeService.generateQRCode(dog.getId(), baseUrl);
            String qrContent = qrCodeService.getQRCodeContent(dog.getId(), baseUrl);

            dog.setQrCodePath(qrPath);
            dog = dogRepository.save(dog);

            QRCode qrCode = QRCode.builder()
                    .dog(dog)
                    .qrCodePath(qrPath)
                    .qrCodeContent(qrContent)
                    .build();
            qrCodeRepository.save(qrCode);
        } catch (Exception e) {
            log.error("QR Code generation failed for dog {}: {}", dog.getId(), e.getMessage());
        }

        return mapToResponse(dog);
    }

    public List<DogDTOs.DogResponse> getMyDogs() {
        User currentUser = getCurrentUser();
        return dogRepository.findByOwner(currentUser)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public DogDTOs.DogResponse getDogById(Long id) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dog not found with id: " + id));
        return mapToResponse(dog);
    }

    public List<DogDTOs.DogResponse> getAllDogs() {
        return dogRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public DogDTOs.DogResponse updateDog(Long id, DogDTOs.DogRequest request) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dog not found"));
        dog.setName(request.getName());
        dog.setBreed(request.getBreed());
        dog.setAge(request.getAge());
        dog.setGender(request.getGender());
        dog.setWeight(request.getWeight());
        return mapToResponse(dogRepository.save(dog));
    }

    @Transactional
    public void deleteDog(Long id) {
        dogRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private DogDTOs.DogResponse mapToResponse(Dog dog) {
        DogDTOs.DogResponse res = new DogDTOs.DogResponse();
        res.setId(dog.getId());
        res.setName(dog.getName());
        res.setBreed(dog.getBreed());
        res.setAge(dog.getAge());
        res.setGender(dog.getGender());
        res.setWeight(dog.getWeight());
        res.setProfileImageUrl(dog.getProfileImageUrl());
        res.setQrCodePath(dog.getQrCodePath());
        res.setOwnerId(dog.getOwner().getId());
        res.setOwnerName(dog.getOwner().getFullName());
        res.setCreatedAt(dog.getCreatedAt());
        return res;
    }
}