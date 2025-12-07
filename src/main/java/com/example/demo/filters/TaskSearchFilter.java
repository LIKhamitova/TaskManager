package com.example.demo.filters;

import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;

public record  TaskSearchFilter(
        TaskPriority taskPriority,
        TaskStatus taskStatus,
        Integer pageSize,
        Integer pageNumber
)
{
}
