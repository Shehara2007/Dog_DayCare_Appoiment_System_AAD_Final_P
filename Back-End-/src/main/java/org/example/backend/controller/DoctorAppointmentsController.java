package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.doctorAppointmentsDTO;
import org.example.backend.service.custom.DoctorAppointmentsService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/appointments")
@CrossOrigin
public class DoctorAppointmentsController {

    private final DoctorAppointmentsService doctorAppointmentsService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveAppointment(@RequestBody doctorAppointmentsDTO dto) {
        doctorAppointmentsService.saveAppointment(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Saved", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateAppointment(@RequestBody doctorAppointmentsDTO dto) {
        doctorAppointmentsService.updateAppointment(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Updated", null), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<APIResponse<String>> deleteAppointment(@RequestBody doctorAppointmentsDTO dto) {
        doctorAppointmentsService.deleteAppointment(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Deleted", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<doctorAppointmentsDTO>>> getAllAppointments() {
        List<doctorAppointmentsDTO> appointments = doctorAppointmentsService.getAllAppointments();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", appointments), HttpStatus.OK);
    }
}