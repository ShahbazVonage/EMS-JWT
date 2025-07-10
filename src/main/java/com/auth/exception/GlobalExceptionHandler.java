package com.auth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , String>> handleValidationException(MethodArgumentNotValidException ex){
        Map<String , String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField()  , error.getDefaultMessage()));

        return new ResponseEntity<>(errors , HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDatabaseException(DataIntegrityViolationException ex){
        Map<String , Object> error = new HashMap<>();
        error.put("timestamp" , LocalDateTime.now());
        error.put("message" , ex.getMessage());
        error.put("status" , HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex){
        Map<String , Object> error = new HashMap<>();
        error.put("timestamp" , LocalDateTime.now());
        error.put("message" , ex.getMessage());
        error.put("status" , HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(error , HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialException(BadCredentialsException ex){
        Map<String , Object> error = new HashMap<>();
        error.put("timestamp" , LocalDateTime.now());
        error.put("message" , ex.getMessage());
        error.put("status" , HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        Map<String , Object> error = new HashMap<>();
        error.put("timestamp" , LocalDateTime.now());
        error.put("message" , ex.getMessage());
        error.put("status" , HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

}
