package com.eduaircontrol.backend.modules.classrooms.domain.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
