package com.example.demo.exception;

import java.time.LocalDateTime;

public record ErrorDto(
        String message,
        String detailMessage,
        LocalDateTime errorTime
) {
}
