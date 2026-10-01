package com.eduaircontrol.backend.modules.classrooms.domain.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
