package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@ToString(exclude = {"users", "task_group"})
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_group_id")
    private TaskGroup taskGroup;

    @Column(name = "title", length = 100)
    @NotBlank(message = "Task title cannot be empty or null")
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority;

    @Column(name = "start_date")
    @Builder.Default
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "planned_hours")
    private Double plannedHours;

    @Column(name = "actual_hours")
    private Double actualHours;

    @Column(name = "create_ts")
    private LocalDateTime createTs;

    @Column(name = "update_ts")
    private LocalDateTime updateTs;

    @PrePersist
    protected void createTask() {
        createTs = LocalDateTime.now();
        updateTs = LocalDateTime.now();
    }
    @PreUpdate
    protected void updateTask() {
        updateTs = LocalDateTime.now();
    }

}
