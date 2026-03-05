package org.example.backend.service.custom;

import org.example.backend.dto.DoctorDTO;

import java.util.List;

public interface DoctorService {
    void saveDoctor(DoctorDTO dto);
    void updateDoctor(DoctorDTO dto);
    List<DoctorDTO> getAllDoctors();
    DoctorDTO getDoctorById(int id);
    void deleteDoctor(int id);
}