package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.healthReportsDTO;
import org.example.backend.service.custom.HealthReportsService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/healthReport")
@CrossOrigin

public class HealthReportsController {

    private final HealthReportsService healthReportsService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveReport(@RequestBody healthReportsDTO reportDTO){

        healthReportsService.saveReport(reportDTO);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Report Saved",null),
                HttpStatus.CREATED);

    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateReport(@RequestBody healthReportsDTO reportDTO){

        healthReportsService.updateReport(reportDTO);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Report Updated",null),
                HttpStatus.OK);

    }

    @DeleteMapping
    public ResponseEntity<APIResponse<String>> deleteReport(@RequestBody healthReportsDTO reportDTO){

        healthReportsService.deleteReport(reportDTO);

        return new ResponseEntity<>(
                new APIResponse<>(200,"Report Deleted",null),
                HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<APIResponse<List<healthReportsDTO>>> getAllReports(){

        List<healthReportsDTO> reports = healthReportsService.getAllReports();

        return new ResponseEntity<>(
                new APIResponse<>(200,"Success",reports),
                HttpStatus.OK);

    }

}