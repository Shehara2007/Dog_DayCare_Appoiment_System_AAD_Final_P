package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.dogEntity;
import org.example.backend.Repository.DogRepository;
import org.example.backend.dto.dogDTO;
import org.example.backend.exeception.DogNotFoundException;
import org.example.backend.service.custom.DogService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveDog(dogDTO dogDTO) {
        dogDTO.setDogID(0);
        dogEntity dog = modelMapper.map(dogDTO, dogEntity.class);
        dogRepository.save(dog);
    }

    @Override
    public void updateDog(dogDTO dogDTO) {
        dogEntity existingDog = dogRepository
                .findById(dogDTO.getDogID())
                .orElseThrow(() -> new DogNotFoundException(
                        "Dog not found with ID: " + dogDTO.getDogID()));

        existingDog.setDogName(dogDTO.getDogName());
        existingDog.setDogBreed(dogDTO.getDogBreed());
        existingDog.setAge(dogDTO.getAge());
        existingDog.setGender(dogDTO.getGender());
        existingDog.setOwnerID(dogDTO.getOwnerID());
        existingDog.setVaccinationExpiry(dogDTO.getVaccinationExpiry());
        existingDog.setBehaviourType(dogDTO.getBehaviourType());
        existingDog.setHealthStatus(dogDTO.getHealthStatus());
        existingDog.setSpecialNotes(dogDTO.getSpecialNotes());

        dogRepository.saveAndFlush(existingDog);
    }

    @Override
    public void deleteDog(dogDTO dogDTO) {
        dogEntity existingDog = dogRepository
                .findById(dogDTO.getDogID())
                .orElseThrow(() -> new DogNotFoundException(
                        "Dog not found with ID: " + dogDTO.getDogID()));

        dogRepository.delete(existingDog);
        dogRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public List<dogDTO> getAllDogs() {
        List<dogEntity> list = dogRepository.findAll();
        return modelMapper.map(list, new TypeToken<List<dogDTO>>() {}.getType());
    }
}