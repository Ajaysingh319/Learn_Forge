package com.learnforge.server.exception;

import com.learnforge.server.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildError(
                request,
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                details
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildError(
                request,
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return buildError(
                request,
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request
    ) {
        return buildError(
                request,
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                java.util.Collections.emptyList()
        );
    }

    @ExceptionHandler(AiGenerationException.class)
    public ResponseEntity<ApiErrorResponse> handleAiGeneration(
            AiGenerationException ex,
            HttpServletRequest request
    ) {
        return buildError(
                request,
                HttpStatus.BAD_GATEWAY,
                ex.getMessage(),
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error",
                List.of(ex.getClass().getSimpleName())
        );
    }

    private ResponseEntity<ApiErrorResponse> buildError(
            HttpServletRequest request,
            HttpStatus status,
            String message,
            List<String> details
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                request.getRequestURI(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details,
                Instant.now()
        );
        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
