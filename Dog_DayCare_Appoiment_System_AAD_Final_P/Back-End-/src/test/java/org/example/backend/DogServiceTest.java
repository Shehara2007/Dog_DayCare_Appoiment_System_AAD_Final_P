package org.example.backend;

import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.dto.CreateDogRequest;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.dto.UpdateDogRequest;
import org.example.backend.exeception.ResourceNotFoundException;
import org.example.backend.service.DogService;
import org.example.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DogServiceTest {

    @Autowired private DogService dogService;
    @Autowired private UserService userService;

    @Test
    void shouldUpdateAndDeleteDog() {
        User owner = createPetOwner("Owner One", "dog-owner@test.com");

        CreateDogRequest create = new CreateDogRequest();
        create.setName("Rex");
        create.setBreed("Labrador");
        create.setDateOfBirth(LocalDate.of(2022, 1, 10));
        create.setOwnerId(owner.getId());

        Dog created = dogService.register(create);

        UpdateDogRequest update = new UpdateDogRequest();
        update.setName("Rex Updated");
        update.setBreed("Golden Retriever");
        update.setDateOfBirth(LocalDate.of(2021, 7, 15));

        Dog updated = dogService.update(created.getId(), update);
        assertEquals("Rex Updated", updated.getName());
        assertEquals("Golden Retriever", updated.getBreed());
        assertEquals(LocalDate.of(2021, 7, 15), updated.getDateOfBirth());

        dogService.delete(created.getId());
        assertThrows(ResourceNotFoundException.class, () -> dogService.getById(created.getId()));
    }

    private User createPetOwner(String name, String email) {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setEmail(email);
        req.setPhone("0770000000");
        req.setPassword("Test@1234");
        req.setRole(UserRole.PET_OWNER);
        return userService.create(req);
    }
}

