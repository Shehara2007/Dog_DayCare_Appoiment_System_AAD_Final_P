package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.CaretakerDTO;
import org.example.backend.service.custom.CaretakerService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/caretakers")
@CrossOrigin
public class CaretakerController {

    private final CaretakerService caretakerService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveCaretaker(@RequestBody CaretakerDTO dto) {
        caretakerService.saveCaretaker(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Caretaker Saved Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateCaretaker(@RequestBody CaretakerDTO dto) {
        caretakerService.updateCaretaker(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Caretaker Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<CaretakerDTO>>> getAllCaretakers() {
        List<CaretakerDTO> caretakers = caretakerService.getAllCaretakers();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", caretakers), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CaretakerDTO>> getCaretakerById(@PathVariable int id) {
        CaretakerDTO caretaker = caretakerService.getCaretakerById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", caretaker), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteCaretaker(@PathVariable int id) {
        caretakerService.deleteCaretaker(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Caretaker Deleted Successfully", null), HttpStatus.OK);
    }
}