package com.example.iaassessment.controller;

import com.example.iaassessment.dto.ApiErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> badRequest(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(new ApiErrorDto(ex.getMessage())); }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDto> conflict(IllegalStateException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorDto(ex.getMessage())); }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorDto> forbidden(AccessDeniedException ex) { return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiErrorDto(ex.getMessage())); }
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiErrorDto> unauthorized(RuntimeException ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorDto("Credenciais inválidas")); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> validation(MethodArgumentNotValidException ex) { return ResponseEntity.badRequest().body(new ApiErrorDto("Dados inválidos")); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> fallback(Exception ex) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorDto("Erro inesperado")); }
}
