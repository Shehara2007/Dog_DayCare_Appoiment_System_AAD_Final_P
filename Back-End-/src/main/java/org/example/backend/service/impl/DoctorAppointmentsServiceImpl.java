package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.doctorAppointmentsEntity;
import org.example.backend.Repository.DoctorAppointmentsRepository;
import org.example.backend.dto.doctorAppointmentsDTO;
import org.example.backend.exeception.DoctorAppointmentsNotFoundException;
import org.example.backend.service.custom.DoctorAppointmentsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class DoctorAppointmentsServiceImpl implements DoctorAppointmentsService {

    private final DoctorAppointmentsRepository doctorAppointmentsRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveAppointment(doctorAppointmentsDTO dto) {
        dto.setAppointmentID(0);
        doctorAppointmentsEntity entity = modelMapper.map(dto, doctorAppointmentsEntity.class);
        doctorAppointmentsRepository.save(entity);
    }

    @Override
    public void updateAppointment(doctorAppointmentsDTO dto) {
        doctorAppointmentsEntity existing = doctorAppointmentsRepository
                .findById(dto.getAppointmentID())
                .orElseThrow(() -> new DoctorAppointmentsNotFoundException(
                        "Appointment not found with ID: " + dto.getAppointmentID()));

        existing.setDoctorName(dto.getDoctorName());
        existing.setDogID(dto.getDogID());
        existing.setAppointmentDate(dto.getAppointmentDate());
        existing.setAppointmentTime(dto.getAppointmentTime());
        existing.setReason(dto.getReason());

        doctorAppointmentsRepository.saveAndFlush(existing);
    }

    @Override
    public void deleteAppointment(doctorAppointmentsDTO dto) {
        doctorAppointmentsEntity existing = doctorAppointmentsRepository
                .findById(dto.getAppointmentID())
                .orElseThrow(() -> new DoctorAppointmentsNotFoundException(
                        "Appointment not found with ID: " + dto.getAppointmentID()));

        doctorAppointmentsRepository.delete(existing);
        doctorAppointmentsRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public List<doctorAppointmentsDTO> getAllAppointments() {
        List<doctorAppointmentsEntity> list = doctorAppointmentsRepository.findAll();
        return modelMapper.map(list, new TypeToken<List<doctorAppointmentsDTO>>() {}.getType());
    }
}