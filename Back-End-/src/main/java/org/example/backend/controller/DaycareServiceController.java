package org.example.backend.controller;

import lombok.RequiredArgsConstructor;

import org.example.backend.dto.DaycareServiceDTO;
import org.example.backend.service.custom.DaycareServiceService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/services")
@CrossOrigin
public class DaycareServiceController {

    private final DaycareServiceService daycareServiceService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveService(@RequestBody DaycareServiceDTO dto) {
        daycareServiceService.saveService(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Service Saved Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateService(@RequestBody DaycareServiceDTO dto) {
        daycareServiceService.updateService(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Service Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<DaycareServiceDTO>>> getAllServices() {
        List<DaycareServiceDTO> services = daycareServiceService.getAllServices();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", services), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<DaycareServiceDTO>> getServiceById(@PathVariable int id) {
        DaycareServiceDTO service = daycareServiceService.getServiceById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", service), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteService(@PathVariable int id) {
        daycareServiceService.deleteService(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Service Deleted Successfully", null), HttpStatus.OK);
    }
}