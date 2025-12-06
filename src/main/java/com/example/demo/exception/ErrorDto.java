package com.example.demo.exception;

import java.time.LocalDateTime;

public record ErrorUserDTO(
        String message,
        String detailMessage,
        LocalDateTime errorTime
) {
}
