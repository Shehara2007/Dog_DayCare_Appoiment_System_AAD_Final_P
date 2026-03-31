package org.example.backend.exeception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

