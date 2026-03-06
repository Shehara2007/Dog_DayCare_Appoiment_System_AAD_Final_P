package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.dogDTO;
import org.example.backend.service.custom.DogService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dog")
@CrossOrigin
public class DogController {

    private final DogService dogService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveDog(@RequestBody dogDTO dogDTO) {
        dogService.saveDog(dogDTO);
        return new ResponseEntity<>(new APIResponse<>(200, "Dog Saved", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateDog(@RequestBody dogDTO dogDTO) {
        dogService.updateDog(dogDTO);
        return new ResponseEntity<>(new APIResponse<>(200, "Dog Updated", null), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<APIResponse<String>> deleteDog(@RequestBody dogDTO dogDTO) {
        dogService.deleteDog(dogDTO);
        return new ResponseEntity<>(new APIResponse<>(200, "Dog Deleted", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<dogDTO>>> getAllDogs() {
        List<dogDTO> dogs = dogService.getAllDogs();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", dogs), HttpStatus.OK);
    }
}