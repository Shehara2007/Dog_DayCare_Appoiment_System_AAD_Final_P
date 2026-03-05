package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.DaycareService;
import org.example.backend.Repository.DaycareServiceRepository;
import org.example.backend.dto.DaycareServiceDTO;
import org.example.backend.service.custom.DaycareServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DaycareServiceServiceImpl implements DaycareServiceService {

    private final DaycareServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveService(DaycareServiceDTO dto) {
        serviceRepository.save(modelMapper.map(dto, DaycareService.class));
    }

    @Override
    public void updateService(DaycareServiceDTO dto) {
        if (!serviceRepository.existsById(dto.getServiceId())) {
            throw new RuntimeException("Service not found with ID: " + dto.getServiceId());
        }
        serviceRepository.save(modelMapper.map(dto, DaycareService.class));
    }

    @Override
    public List<DaycareServiceDTO> getAllServices() {
        List<DaycareService> services = serviceRepository.findAll();
        return modelMapper.map(services, new TypeToken<List<DaycareServiceDTO>>() {}.getType());
    }

    @Override
    public DaycareServiceDTO getServiceById(int id) {
        DaycareService service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + id));
        return modelMapper.map(service, DaycareServiceDTO.class);
    }

    @Override
    public void deleteService(int id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found with ID: " + id);
        }
        serviceRepository.deleteById(id);
    }
}