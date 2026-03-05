package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.DogDTO;
import org.example.backend.service.custom.DogService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dogs")
@CrossOrigin
public class DogController {

    private final DogService dogService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveDog(@RequestBody DogDTO dto) {
        dogService.saveDog(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "Dog Registered Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateDog(@RequestBody DogDTO dto) {
        dogService.updateDog(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "Dog Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<DogDTO>>> getAllDogs() {
        List<DogDTO> dogs = dogService.getAllDogs();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", dogs), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<DogDTO>> getDogById(@PathVariable int id) {
        DogDTO dog = dogService.getDogById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", dog), HttpStatus.OK);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<APIResponse<List<DogDTO>>> getDogsByOwner(@PathVariable int ownerId) {
        List<DogDTO> dogs = dogService.getDogsByOwner(ownerId);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", dogs), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteDog(@PathVariable int id) {
        dogService.deleteDog(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Dog Deleted Successfully", null), HttpStatus.OK);
    }
}