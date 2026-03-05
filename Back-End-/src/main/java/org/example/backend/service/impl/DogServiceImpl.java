package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.example.backend.Entity.Dog;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.dto.DogDTO;
import org.example.backend.service.custom.DogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveDog(DogDTO dogDTO) {
        User owner = userRepository.findById(dogDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + dogDTO.getOwnerId()));
        Dog dog = modelMapper.map(dogDTO, Dog.class);
        dog.setOwner(owner);
        dogRepository.save(dog);
    }

    @Override
    public void updateDog(DogDTO dogDTO) {
        if (!dogRepository.existsById(dogDTO.getDogId())) {
            throw new RuntimeException("Dog not found with ID: " + dogDTO.getDogId());
        }
        User owner = userRepository.findById(dogDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + dogDTO.getOwnerId()));
        Dog dog = modelMapper.map(dogDTO, Dog.class);
        dog.setOwner(owner);
        dogRepository.save(dog);
    }

    @Override
    public List<DogDTO> getAllDogs() {
        List<Dog> dogs = dogRepository.findAll();
        return modelMapper.map(dogs, new TypeToken<List<DogDTO>>() {}.getType());
    }

    @Override
    public List<DogDTO> getDogsByOwner(int ownerId) {
        List<Dog> dogs = dogRepository.findByOwner_UserId(ownerId);
        return modelMapper.map(dogs, new TypeToken<List<DogDTO>>() {}.getType());
    }

    @Override
    public DogDTO getDogById(int id) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dog not found with ID: " + id));
        return modelMapper.map(dog, DogDTO.class);
    }

    @Override
    public void deleteDog(int id) {
        if (!dogRepository.existsById(id)) {
            throw new RuntimeException("Dog not found with ID: " + id);
        }
        dogRepository.deleteById(id);
    }
}