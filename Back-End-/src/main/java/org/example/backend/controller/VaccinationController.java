package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.VaccinationDTO;
import org.example.backend.service.custom.VaccinationService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/vaccinations")
@CrossOrigin
public class VaccinationController {

    private final VaccinationService vaccinationService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveVaccination(@RequestBody VaccinationDTO dto) {
        vaccinationService.saveVaccination(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Vaccination Record Saved", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateVaccination(@RequestBody VaccinationDTO dto) {
        vaccinationService.updateVaccination(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Vaccination Record Updated", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<VaccinationDTO>>> getAllVaccinations() {
        List<VaccinationDTO> vaccinations = vaccinationService.getAllVaccinations();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", vaccinations), HttpStatus.OK);
    }

    @GetMapping("/dog/{dogId}")
    public ResponseEntity<APIResponse<List<VaccinationDTO>>> getVaccinationsByDog(@PathVariable int dogId) {
        List<VaccinationDTO> vaccinations = vaccinationService.getVaccinationsByDog(dogId);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", vaccinations), HttpStatus.OK);
    }

    @GetMapping("/expiring")
    public ResponseEntity<APIResponse<List<VaccinationDTO>>> getExpiringVaccinations() {
        List<VaccinationDTO> vaccinations = vaccinationService.getExpiringVaccinations();
        return new ResponseEntity<>(new APIResponse<>(200, "Expiring Vaccinations", vaccinations), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteVaccination(@PathVariable int id) {
        vaccinationService.deleteVaccination(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Vaccination Record Deleted", null), HttpStatus.OK);
    }
}