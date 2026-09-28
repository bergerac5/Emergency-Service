package com.eards.emergency_service.exceptions;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.eards.emergency_service.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExpextionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExpextionHandler.class);

        // handleEmergencyNotFoundException
        @ExceptionHandler(EmergencyNotFoundException.class)
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

        // is for validation errors
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getDefaultMessage() == null ? "Invalid value"
                                                : error.getDefaultMessage())
                                .collect(Collectors.joining("; "));

                ErrorResponse error = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                message,
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // is for when access code is not found
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

        // is for unexpected errors
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpectedError(
                        Exception ex,
                        HttpServletRequest request) {

                ErrorResponse error = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "Something went wrong while processing your request. Please try again.",
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        // is for invalid status transitions
        @ExceptionHandler(InvalidStatusTransitionException.class)
        public ResponseEntity<ErrorResponse> handleInvalidStatusTransition(
                        InvalidStatusTransitionException ex,
                        HttpServletRequest request) {
                logger.error("Invalid status transition", ex);
                ErrorResponse errorResponse = new ErrorResponse(
                                Instant.now(),
                                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                                HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
                                request.getRequestURI(),
                                ex.getMessage());
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(errorResponse);
        }

        // is for invalid type conversions
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
                return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + ex.getName() + "'", request);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleUnreadable(
                        HttpMessageNotReadableException ex, HttpServletRequest request) {
                return build(HttpStatus.BAD_REQUEST, "Request body is missing or malformed", request);
        }

        private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(
                                Instant.now(), status.value(), status.getReasonPhrase(), message,
                                request.getRequestURI());
                return ResponseEntity.status(status).body(error);
        }
}
