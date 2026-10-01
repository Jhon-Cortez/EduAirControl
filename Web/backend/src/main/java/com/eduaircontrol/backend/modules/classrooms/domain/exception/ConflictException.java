package com.eduaircontrol.backend.modules.classrooms.domain.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
