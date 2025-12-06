package com.example.demo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskPriority {
    LOW("Низкий"),
   MEDIUM("Средний"),
   HIGH("Высокий");

   private final String name;
}