package org.example.backend.service.custom;

import org.example.backend.dto.CaretakerDTO;

import java.util.List;

public interface CaretakerService {
    void saveCaretaker(CaretakerDTO dto);
    void updateCaretaker(CaretakerDTO dto);
    List<CaretakerDTO> getAllCaretakers();
    CaretakerDTO getCaretakerById(int id);
    void deleteCaretaker(int id);
}