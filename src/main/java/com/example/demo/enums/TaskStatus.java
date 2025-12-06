package com.example.demo.entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    PLANNED("Запланирована"),
    IN_PROGRESS("В работе"),
    COMPLETED("Сделана"),
    CANCELLED("Отменена");

    private final String displayName;

    @Getter
    @RequiredArgsConstructor
    public enum TaskPriority {
        LOW("Низкий"),
        MEDIUM("Средний"),
        HIGH("Высокий");

        private final String name;
    }
}
