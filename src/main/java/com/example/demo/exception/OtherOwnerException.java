package com.example.demo.exception;

public class TaskGroupNotFoundException extends RuntimeException {
    public TaskGroupNotFoundException(String message) {
        super(message);
    }
}
