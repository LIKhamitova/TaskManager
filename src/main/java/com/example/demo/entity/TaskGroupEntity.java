package com.example.demo.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TaskGroup extends BasicEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "taskGroup")
    private List<TaskEntity> taskEntities = new ArrayList<>();

    @Column(name="name",  length = 100)
    @NotBlank(message = "Group name cannot be empty or null")
    private String name;

    @Column(name="description", length = 500)
    private String description;

}


