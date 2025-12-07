package com.example.demo.exception;

import lombok.Getter;

import org.springframework.security.core.AuthenticationException;


@Getter
public class UserNotAuthenticatedException extends AuthenticationException {
    public UserNotAuthenticatedException(String message) {

        super(message);
    }
}
