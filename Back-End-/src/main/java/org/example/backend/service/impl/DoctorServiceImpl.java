package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.Repository.DoctorRepository;
import org.example.backend.dto.DoctorDTO;
import org.example.backend.service.custom.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveDoctor(DoctorDTO dto) {
        doctorRepository.save(modelMapper.map(dto, Doctor.class));
    }

    @Override
    public void updateDoctor(DoctorDTO dto) {
        if (!doctorRepository.existsById(dto.getDoctorId())) {
            throw new RuntimeException("Doctor not found with ID: " + dto.getDoctorId());
        }
        doctorRepository.save(modelMapper.map(dto, Doctor.class));
    }

    @Override
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return modelMapper.map(doctors, new TypeToken<List<DoctorDTO>>() {}.getType());
    }

    @Override
    public DoctorDTO getDoctorById(int id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
        return modelMapper.map(doctor, DoctorDTO.class);
    }

    @Override
    public void deleteDoctor(int id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found with ID: " + id);
        }
        doctorRepository.deleteById(id);
    }
}