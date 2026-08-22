package com.example.leavemanagement.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    record ErrorResponse(Instant timestamp,int status,String error,String message) {}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e){String msg=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining(", "));return response(HttpStatus.BAD_REQUEST,msg);}
    @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException e){return response(HttpStatus.BAD_REQUEST,e.getMessage());}
    @ExceptionHandler(IllegalStateException.class) ResponseEntity<ErrorResponse> conflict(IllegalStateException e){return response(HttpStatus.CONFLICT,e.getMessage());}
    private ResponseEntity<ErrorResponse> response(HttpStatus s,String m){return ResponseEntity.status(s).body(new ErrorResponse(Instant.now(),s.value(),s.getReasonPhrase(),m));}
}
