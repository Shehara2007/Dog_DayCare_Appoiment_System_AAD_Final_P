package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.DoctorDTO;
import org.example.backend.service.custom.DoctorService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/doctors")
@CrossOrigin
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveDoctor(@RequestBody DoctorDTO dto) {
        doctorService.saveDoctor(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Doctor Saved Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateDoctor(@RequestBody DoctorDTO dto) {
        doctorService.updateDoctor(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Doctor Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<DoctorDTO>>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", doctors), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<DoctorDTO>> getDoctorById(@PathVariable int id) {
        DoctorDTO doctor = doctorService.getDoctorById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", doctor), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteDoctor(@PathVariable int id) {
        doctorService.deleteDoctor(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Doctor Deleted Successfully", null), HttpStatus.OK);
    }
}