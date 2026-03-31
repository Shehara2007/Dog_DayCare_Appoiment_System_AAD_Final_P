package org.example.backend;

import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.dto.BookDaycareAppointmentRequest;
import org.example.backend.dto.CreateDogRequest;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.DaycareAppointmentService;
import org.example.backend.service.DogService;
import org.example.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DaycareAppointmentServiceTest {

    @Autowired private DaycareAppointmentService daycareAppointmentService;
    @Autowired private UserService userService;
    @Autowired private DogService dogService;

    @Test
    void shouldRejectFourthOverlappingAppointmentForSameCaretaker() {
        User owner     = createUser("Owner",     "owner@test.com",     UserRole.PET_OWNER);
        User caretaker = createUser("Caretaker", "caretaker@test.com", UserRole.CARETAKER);

        LocalDateTime start = LocalDateTime.now().plusDays(1)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        Long dog1 = createDog(owner.getId(), "Dog1");
        Long dog2 = createDog(owner.getId(), "Dog2");
        Long dog3 = createDog(owner.getId(), "Dog3");
        Long dog4 = createDog(owner.getId(), "Dog4");

        assertDoesNotThrow(() -> daycareAppointmentService.book(request(dog1, caretaker.getId(), start, end)));
        assertDoesNotThrow(() -> daycareAppointmentService.book(request(dog2, caretaker.getId(), start, end)));
        assertDoesNotThrow(() -> daycareAppointmentService.book(request(dog3, caretaker.getId(), start, end)));

        assertThrows(BusinessException.class,
                () -> daycareAppointmentService.book(request(dog4, caretaker.getId(), start, end)));
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private User createUser(String name, String email, UserRole role) {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setEmail(email);
        req.setPhone("0770000000");
        req.setPassword("Test@1234");
        req.setRole(role);
        return userService.create(req);
    }

    private Long createDog(Long ownerId, String name) {
        CreateDogRequest req = new CreateDogRequest();
        req.setName(name);
        req.setBreed("Mixed");
        req.setOwnerId(ownerId);
        return dogService.register(req).getId();
    }

    private BookDaycareAppointmentRequest request(Long dogId, Long caretakerId,
                                                   LocalDateTime start, LocalDateTime end) {
        BookDaycareAppointmentRequest req = new BookDaycareAppointmentRequest();
        req.setDogId(dogId);
        req.setCaretakerId(caretakerId);
        req.setStartTime(start);
        req.setEndTime(end);
        return req;
    }
}
