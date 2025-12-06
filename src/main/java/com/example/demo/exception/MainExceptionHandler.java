package com.example.demo.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( exception = {
            UserAlreadyExistsException.class,
            HttpMessageNotReadableException .class })
    public ResponseEntity<ErrorUserDTO> errorCreatedItem (Exception e) {
        log.error("Handle exception", e);

        var errorDTO = new ErrorUserDTO(
                "Could not create record",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorUserDTO> notValidation(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);

        List<String> errorDetails = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Field '%s': %s (actual value: '%s')",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .toList();

        var errorDTO = new ErrorUserDTO(
                "Error validation",
                String.join("; ", errorDetails),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDTO);
    }

}



