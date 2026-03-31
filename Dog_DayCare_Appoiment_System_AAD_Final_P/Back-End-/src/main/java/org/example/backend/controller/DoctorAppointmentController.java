package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.DoctorAppointment;
import org.example.backend.dto.CreateDoctorAppointmentRequest;
import org.example.backend.dto.UpdateDoctorAppointmentStatusRequest;
import org.example.backend.service.DoctorAppointmentService;
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
@RequestMapping("/api/v1/doctor-appointments")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final DoctorAppointmentService doctorAppointmentService;

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody CreateDoctorAppointmentRequest request) {
        return mapAppointment(doctorAppointmentService.create(request));
    }

    @PatchMapping("/{appointmentId}/status")
    public Map<String, Object> updateStatus(@PathVariable Long appointmentId,
                                            @Valid @RequestBody UpdateDoctorAppointmentStatusRequest request) {
        return mapAppointment(doctorAppointmentService.updateStatus(appointmentId, request.getStatus()));
    }

    @GetMapping(params = "dogId")
    public List<Map<String, Object>> byDog(@RequestParam Long dogId) {
        return doctorAppointmentService.getByDog(dogId).stream().map(this::mapAppointment).toList();
    }

    @GetMapping(params = "ownerId")
    public List<Map<String, Object>> byOwner(@RequestParam Long ownerId) {
        return doctorAppointmentService.getByOwner(ownerId).stream().map(this::mapAppointment).toList();
    }

    @GetMapping(params = "doctorId")
    public List<Map<String, Object>> byDoctor(@RequestParam Long doctorId) {
        return doctorAppointmentService.getByDoctor(doctorId).stream().map(this::mapAppointment).toList();
    }

    private Map<String, Object> mapAppointment(DoctorAppointment appointment) {
        return Map.of(
                "id", appointment.getId(),
                "dogId", appointment.getDog().getId(),
                "ownerId", appointment.getOwner().getId(),
                "doctorId", appointment.getDoctor().getId(),
                "appointmentTime", appointment.getAppointmentTime(),
                "status", appointment.getStatus(),
                "notes", appointment.getNotes() == null ? "" : appointment.getNotes()
        );
    }
}

