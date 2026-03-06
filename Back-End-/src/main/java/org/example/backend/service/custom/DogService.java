package org.example.backend.service.custom;

import org.example.backend.dto.dogDTO;

import java.util.List;

public interface DogService {
    void saveDog(dogDTO dogDTO);
    void updateDog(dogDTO dogDTO);
    void deleteDog(dogDTO dogDTO);
    List<dogDTO> getAllDogs();
}