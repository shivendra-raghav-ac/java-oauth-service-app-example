package com.example.oktausersvc.exception;

import com.example.oktausersvc.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UpstreamAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(UpstreamAuthException ex) {
        return new ResponseEntity<>(new ErrorResponse("UPSTREAM_AUTH_ERROR", ex.getMessage()), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_ERROR", "Unexpected error"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
