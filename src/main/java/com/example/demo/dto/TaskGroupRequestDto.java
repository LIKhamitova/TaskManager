package com.example.demo.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupDTORequest {
        @NotBlank
        private String name;
        @Size(max = 500)
        private String description;
        private List<Long> taskListId = new ArrayList<>();
    }


