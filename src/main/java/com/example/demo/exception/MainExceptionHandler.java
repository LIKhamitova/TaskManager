package com.example.demo.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Value not found", e);

        var errorDTO = new ErrorDto(
                "Not found",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDTO);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public ResponseEntity<ErrorDto> taskNotCurrentUserException(UserNotAuthenticatedException e){
        log.error("Exception - User is not authenticated", e);

        var errorDTO = new ErrorDto(
                "AuthenticationException",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorDTO);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> notValidation(MethodArgumentNotValidException e) {
        log.error("Exception - MethodArgumentNotValidException", e);

        List<String> errorDetails = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("Field '%s': %s (actual value: '%s')",
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .toList();

        var errorDTO = new ErrorDto(
                "Error validation",
                String.join("; ", errorDetails),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDTO);
    }
    @ExceptionHandler(TaskNotCurrentUserException.class)
    public ResponseEntity<ErrorDto> taskNotCurrentUserException(TaskNotCurrentUserException e){
        log.error("Exception - TaskNotCurrentUserException ", e);

        var errorDTO = new ErrorDto(
                "AuthenticationException",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorDTO);

    }
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorDto> handleJwtAuthenticationException(JwtAuthenticationException e) {
        log.warn("JWT token error: {}", e.getMessage()); // warn - не error!

        var errorDTO = new ErrorDto(
                "Invalid token",
                "JWT token is invalid or expired. Please login again.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorDTO);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleOtherAuthenticationException(AuthenticationException e) {
        log.error("Authentication error: {}", e.getMessage());

        var errorDTO = new ErrorDto(
                "Authentication failed",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFound(
            EntityNotFoundException e
    ) {
        log.error("Handle entityNotFoundException", e);

        var errorDTO = new ErrorDto(
                "Entity not found",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDTO);
    }

    @ExceptionHandler({UserAlreadyExistsException.class,
            HttpMessageNotReadableException.class })
    public ResponseEntity<ErrorDto> errorCreatedItem (Exception e) {
        log.error("Handle exception", e);

        var errorDTO = new ErrorDto(
                "Error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorDTO);
    }

    @ExceptionHandler({ OtherOwnerException.class})
    public ResponseEntity<ErrorDto> taskGroupNotFoundException (OtherOwnerException e) {
        log.error("Error- Group is other owner", e);

        var errorDTO = new ErrorDto(
                "Group is other owner",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        log.error("Error", e);

        var errorDTO = new ErrorDto(
                "Error",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDTO);
    }
}



