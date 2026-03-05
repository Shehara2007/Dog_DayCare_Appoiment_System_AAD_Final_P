package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Caretaker;
import org.example.backend.Repository.CaretakerRepository;
import org.example.backend.dto.CaretakerDTO;
import org.example.backend.service.custom.CaretakerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CaretakerServiceImpl implements CaretakerService {

    private final CaretakerRepository caretakerRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveCaretaker(CaretakerDTO dto) {
        caretakerRepository.save(modelMapper.map(dto, Caretaker.class));
    }

    @Override
    public void updateCaretaker(CaretakerDTO dto) {
        if (!caretakerRepository.existsById(dto.getCaretakerId())) {
            throw new RuntimeException("Caretaker not found with ID: " + dto.getCaretakerId());
        }
        caretakerRepository.save(modelMapper.map(dto, Caretaker.class));
    }

    @Override
    public List<CaretakerDTO> getAllCaretakers() {
        List<Caretaker> caretakers = caretakerRepository.findAll();
        return modelMapper.map(caretakers, new TypeToken<List<CaretakerDTO>>() {}.getType());
    }

    @Override
    public CaretakerDTO getCaretakerById(int id) {
        Caretaker caretaker = caretakerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Caretaker not found with ID: " + id));
        return modelMapper.map(caretaker, CaretakerDTO.class);
    }

    @Override
    public void deleteCaretaker(int id) {
        if (!caretakerRepository.existsById(id)) {
            throw new RuntimeException("Caretaker not found with ID: " + id);
        }
        caretakerRepository.deleteById(id);
    }
}