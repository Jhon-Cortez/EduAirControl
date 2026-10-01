package com.eduaircontrol.backend.shared.web;

import com.eduaircontrol.backend.modules.classrooms.domain.exception.ConflictException;
import com.eduaircontrol.backend.modules.classrooms.domain.exception.NotFoundException;
import com.eduaircontrol.backend.modules.classrooms.domain.exception.ValidationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handler global unico: siempre responde {"status": N, "message": "..."} para que
 * apiClient del frontend pueda mostrar el mensaje directamente.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return body(ex.getStatusCode().value(),
                ex.getReason() != null ? ex.getReason() : ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return body(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return body(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidation(ValidationException ex) {
        return body(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Datos inválidos");
        return body(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleUnreadable(Exception ex) {
        return body(HttpStatus.BAD_REQUEST.value(), "Cuerpo de la petición inválido");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return body(HttpStatus.CONFLICT.value(), "La operación viola una restricción de datos");
    }

    @ExceptionHandler({AccessDeniedException.class,
            org.springframework.security.authorization.AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccessDenied(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", HttpStatus.FORBIDDEN.value(),
                        "message", "No tienes permisos para esta operación"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Error inesperado", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> body(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
