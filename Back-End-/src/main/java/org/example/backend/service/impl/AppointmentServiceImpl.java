package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.example.backend.Entity.Appointment;
import org.example.backend.Entity.Caretaker;
import org.example.backend.Entity.DaycareService;
import org.example.backend.Entity.Dog;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.Repository.*;
import org.example.backend.dto.AppointmentDTO;
import org.example.backend.service.custom.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final DaycareServiceRepository daycareServiceRepository;
    private final CaretakerRepository caretakerRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveAppointment(AppointmentDTO dto) {
        // 1. Validate dog exists
        Dog dog = dogRepository.findById(dto.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found with ID: " + dto.getDogId()));

        // 2. Vaccination check
        if (dog.getVaccinationExpiryDate() != null &&
                dog.getVaccinationExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book: " + dog.getDogName() + "'s vaccination has expired. Please renew first.");
        }

        // 3. Owner exists
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + dto.getOwnerId()));

        // 4. Service exists
        DaycareService daycareService = daycareServiceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + dto.getServiceId()));

        // 5. Caretaker capacity check
        Caretaker caretaker = caretakerRepository.findById(dto.getCaretakerId())
                .orElseThrow(() -> new RuntimeException("Caretaker not found with ID: " + dto.getCaretakerId()));

        if (caretaker.getCurrentLoad() >= caretaker.getMaxCapacity()) {
            throw new RuntimeException("Caretaker " + caretaker.getCaretakerName() + " is at full capacity.");
        }

        // 6. Save appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setTimeSlot(dto.getTimeSlot());
        appointment.setNotes(dto.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setDog(dog);
        appointment.setOwner(owner);
        appointment.setDaycareService(daycareService);
        appointment.setCaretaker(caretaker);

        // 7. Update caretaker load
        caretaker.setCurrentLoad(caretaker.getCurrentLoad() + 1);
        caretakerRepository.save(caretaker);

        appointmentRepository.save(appointment);
    }

    @Override
    public void updateAppointment(AppointmentDTO dto) {
        if (!appointmentRepository.existsById(dto.getAppointmentId())) {
            throw new RuntimeException("Appointment not found with ID: " + dto.getAppointmentId());
        }
        Appointment existing = appointmentRepository.findById(dto.getAppointmentId()).get();
        existing.setAppointmentDate(dto.getAppointmentDate());
        existing.setTimeSlot(dto.getTimeSlot());
        existing.setNotes(dto.getNotes());
        existing.setStatus(dto.getStatus());
        appointmentRepository.save(existing);
    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return modelMapper.map(appointments, new TypeToken<List<AppointmentDTO>>() {}.getType());
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByOwner(int ownerId) {
        List<Appointment> appointments = appointmentRepository.findByOwner_UserId(ownerId);
        return modelMapper.map(appointments, new TypeToken<List<AppointmentDTO>>() {}.getType());
    }

    @Override
    public AppointmentDTO getAppointmentById(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        return modelMapper.map(appointment, AppointmentDTO.class);
    }

    @Override
    public void approveAppointment(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void rejectAppointment(int id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        // Release caretaker slot
        Caretaker caretaker = appointment.getCaretaker();
        if (caretaker != null && caretaker.getCurrentLoad() > 0) {
            caretaker.setCurrentLoad(caretaker.getCurrentLoad() - 1);
            caretakerRepository.save(caretaker);
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(int id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with ID: " + id);
        }
        appointmentRepository.deleteById(id);
    }
}