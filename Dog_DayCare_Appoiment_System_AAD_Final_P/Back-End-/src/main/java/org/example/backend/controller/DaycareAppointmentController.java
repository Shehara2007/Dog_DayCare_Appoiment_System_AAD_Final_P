package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.DaycareAppointment;
import org.example.backend.dto.BookDaycareAppointmentRequest;
import org.example.backend.dto.UpdateAppointmentStatusRequest;
import org.example.backend.service.DaycareAppointmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/daycare-appointments")
@RequiredArgsConstructor
public class DaycareAppointmentController {

    private final DaycareAppointmentService appointmentService;

    @PostMapping
    public Map<String, Object> book(@Valid @RequestBody BookDaycareAppointmentRequest request) {
        return mapAppointment(appointmentService.book(request));
    }

    @PatchMapping("/{appointmentId}/status")
    public Map<String, Object> updateStatus(@PathVariable Long appointmentId,
                                            @Valid @RequestBody UpdateAppointmentStatusRequest request) {
        return mapAppointment(appointmentService.updateStatus(appointmentId, request.getStatus()));
    }

    @GetMapping(params = "dogId")
    public List<Map<String, Object>> byDog(@RequestParam Long dogId) {
        return appointmentService.getByDog(dogId).stream().map(this::mapAppointment).toList();
    }

    @GetMapping(params = "caretakerId")
    public List<Map<String, Object>> byCaretaker(@RequestParam Long caretakerId) {
        return appointmentService.getByCaretaker(caretakerId).stream().map(this::mapAppointment).toList();
    }

    private Map<String, Object> mapAppointment(DaycareAppointment appointment) {
        return Map.of(
                "id", appointment.getId(),
                "dogId", appointment.getDog().getId(),
                "caretakerId", appointment.getCaretaker().getId(),
                "startTime", appointment.getStartTime(),
                "endTime", appointment.getEndTime(),
                "status", appointment.getStatus()
        );
    }
}

