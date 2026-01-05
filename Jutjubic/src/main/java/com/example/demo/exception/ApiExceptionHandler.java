package com.example.demo.exception;

import com.example.demo.dto.ApiMessage;
import org.springframework.http.*;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiMessage> validation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiMessage(msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiMessage> illegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiMessage(ex.getMessage()));
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ApiMessage> badCred(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiMessage("Pogresan email ili lozinka."));
    }

    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<ApiMessage> disabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiMessage("Nalog nije aktiviran. Proveri email."));
    }
}
