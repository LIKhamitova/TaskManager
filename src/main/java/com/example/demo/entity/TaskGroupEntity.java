package com.example.demo.entity;
import com.example.demo.enums.TaskGroupStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TaskGroupEntity extends BasicEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "taskGroupEntity")
    private List<TaskEntity> taskEntities = new ArrayList<>();

    @Column(name="name",  length = 100)
    @NotBlank( )
    private String name;

    @Column(name="description", length = 500)
    private String description;

    @Column(name="status", nullable = false )
    @Enumerated(EnumType.STRING)
    TaskGroupStatus status;
}


