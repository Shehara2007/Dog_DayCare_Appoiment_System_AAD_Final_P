package org.example.backend.exeception;

public class DoctorAppointmentsNotFoundException extends RuntimeException {
    public DoctorAppointmentsNotFoundException(String message) {
        super(message);
    }
}