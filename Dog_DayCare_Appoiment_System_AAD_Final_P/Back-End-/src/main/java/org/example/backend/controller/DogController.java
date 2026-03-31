package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.dto.CreateDogRequest;
import org.example.backend.dto.UpdateDogRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.DogService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping
    public Map<String, Object> register(@Valid @RequestBody CreateDogRequest request,
                                        @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() == UserRole.PET_OWNER && !currentUser.getId().equals(request.getOwnerId())) {
            throw new BusinessException("You can only register dogs for your own account");
        }
        return mapDog(dogService.register(request));
    }

    @GetMapping("/{dogId}")
    public Map<String, Object> getById(@PathVariable Long dogId) {
        return mapDog(dogService.getById(dogId));
    }

    @GetMapping
    public List<Map<String, Object>> getDogs(@RequestParam(required = false) Long ownerId,
                                              @AuthenticationPrincipal User currentUser) {
        if (ownerId != null) {
            if (currentUser.getRole() == UserRole.PET_OWNER && !currentUser.getId().equals(ownerId)) {
                throw new BusinessException("You can only view your own dogs");
            }
            return dogService.getByOwner(ownerId).stream().map(this::mapDog).toList();
        }
        
        // For admins, return all dogs. For pet owners, return only their own.
        if (currentUser.getRole() == UserRole.ADMIN) {
            return dogService.getAll().stream().map(this::mapDog).toList();
        } else {
            return dogService.getByOwner(currentUser.getId()).stream().map(this::mapDog).toList();
        }
    }

    @PutMapping("/{dogId}")
    public Map<String, Object> update(@PathVariable Long dogId,
                                      @Valid @RequestBody UpdateDogRequest request,
                                      @AuthenticationPrincipal User currentUser) {
        Dog existingDog = dogService.getById(dogId);
        validateOwnerCanManageDog(existingDog, currentUser);
        return mapDog(dogService.update(dogId, request));
    }

    @DeleteMapping("/{dogId}")
    public Map<String, Object> delete(@PathVariable Long dogId,
                                      @AuthenticationPrincipal User currentUser) {
        Dog existingDog = dogService.getById(dogId);
        validateOwnerCanManageDog(existingDog, currentUser);
        dogService.delete(dogId);
        return Map.of("message", "Dog deleted successfully", "dogId", dogId);
    }

    private void validateOwnerCanManageDog(Dog dog, User currentUser) {
        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }
        if (currentUser.getRole() == UserRole.PET_OWNER && currentUser.getId().equals(dog.getOwner().getId())) {
            return;
        }
        throw new BusinessException("You can only manage your own dogs");
    }

    private Map<String, Object> mapDog(Dog dog) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", dog.getId());
        response.put("name", dog.getName());
        response.put("breed", dog.getBreed());
        response.put("dateOfBirth", dog.getDateOfBirth());
        response.put("ownerId", dog.getOwner().getId());
        response.put("qrCodeBase64", dog.getQrCodeBase64());
        return response;
    }
}

