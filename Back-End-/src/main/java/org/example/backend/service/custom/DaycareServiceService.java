package org.example.backend.service.custom;

import org.example.backend.dto.DaycareServiceDTO;

import java.util.List;

public interface DaycareServiceService {
    void saveService(DaycareServiceDTO dto);
    void updateService(DaycareServiceDTO dto);
    List<DaycareServiceDTO> getAllServices();
    DaycareServiceDTO getServiceById(int id);
    void deleteService(int id);
}