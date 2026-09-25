package com.eards.emergency_service.exceptions;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eards.emergency_service.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExpextionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExpextionHandler.class);

        public ResponseEntity<ErrorResponse> handleEmergencyNotFoundException(
                        EmergencyNotFoundException ex,
                        HttpServletRequest request) {
                logger.error("Emergency not found", ex);
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                request.getRequestURI(),
                                ex.getMessage());
                return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining("; "));

                ErrorResponse error = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                message,
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        @ExceptionHandler(AccessCodeNotFound.class)
        public ResponseEntity<ErrorResponse> handleAccessCodeNotFoundException(
                        AccessCodeNotFound ex,
                        HttpServletRequest request) {
                logger.error("Access code not found", ex);
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                request.getRequestURI(),
                                ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
}
