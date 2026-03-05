package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AppointmentDTO;
import org.example.backend.service.custom.AppointmentService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/appointments")
@CrossOrigin
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveAppointment(@RequestBody AppointmentDTO dto) {
        appointmentService.saveAppointment(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Appointment Booked Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateAppointment(@RequestBody AppointmentDTO dto) {
        appointmentService.updateAppointment(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<AppointmentDTO>>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", appointments), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<AppointmentDTO>> getAppointmentById(@PathVariable int id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", appointment), HttpStatus.OK);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<APIResponse<List<AppointmentDTO>>> getAppointmentsByOwner(@PathVariable int ownerId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByOwner(ownerId);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", appointments), HttpStatus.OK);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<APIResponse<String>> approveAppointment(@PathVariable int id) {
        appointmentService.approveAppointment(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Approved", null), HttpStatus.OK);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<APIResponse<String>> rejectAppointment(@PathVariable int id) {
        appointmentService.rejectAppointment(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Rejected", null), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteAppointment(@PathVariable int id) {
        appointmentService.deleteAppointment(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Appointment Deleted Successfully", null), HttpStatus.OK);
    }
}