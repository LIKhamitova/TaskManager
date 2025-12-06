package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTORequest {
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;
    @NotNull
    @Size(min = 5, message = "Password must be at least 5 characters")
    String password;
}
