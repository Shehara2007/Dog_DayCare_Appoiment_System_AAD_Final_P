package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.HealthReport;
import org.example.backend.dto.HealthReportDTO;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
@CrossOrigin
public class HealthReportController {

    private final HealthReportController dogReportService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveReport(@RequestBody HealthReport dto) {
        dogReportService.saveReport(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Report Saved Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateReport(@RequestBody HealthReport dto) {
        dogReportService.updateReport(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Report Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<HealthReportDTO>>> getAllReports() {
        List<HealthReportDTO> reports = dogReportService.getAllReports();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", reports), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<HealthReportDTO>> getReportById(@PathVariable int id) {
        HealthReportDTO report = dogReportService.getReportById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", report), HttpStatus.OK);
    }

    @GetMapping("/dog/{dogId}")
    public ResponseEntity<APIResponse<List<HealthReportDTO>>> getReportsByDog(@PathVariable int dogId) {
        List<HealthReportDTO> reports = dogReportService.getReportsByDog(dogId);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", reports), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteReport(@PathVariable int id) {
        dogReportService.deleteReport(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Report Deleted Successfully", null), HttpStatus.OK);
    }
}