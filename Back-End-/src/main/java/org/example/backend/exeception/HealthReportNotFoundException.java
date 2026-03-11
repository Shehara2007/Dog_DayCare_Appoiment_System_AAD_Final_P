package org.example.backend.exeception;

public class HealthReportNotFoundException extends RuntimeException {
    public HealthReportNotFoundException(String message) {

        super(message);
    }
}