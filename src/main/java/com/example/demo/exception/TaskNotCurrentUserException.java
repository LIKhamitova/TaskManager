package com.example.demo.exception;

public class TaskNotCurrentUserException extends RuntimeException {
    public TaskNotCurrentUserException(String message) {
        super(message);
    }
}
