package org.example.backend.service.custom;

import org.example.backend.dto.DogDTO;

import java.util.List;

public interface DogService {
    void saveDog(DogDTO dogDTO);
    void updateDog(DogDTO dogDTO);
    List<DogDTO> getAllDogs();
    List<DogDTO> getDogsByOwner(int ownerId);
    DogDTO getDogById(int id);
    void deleteDog(int id);
}