package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/")
@Tag(name = "Authorisation", description = "Authorization block")
public class AuthController {

    private final UserService userService;

  @PostMapping("/sign_up")
  @Operation( summary = "Register new user",
          description = "Creates a new user account in the system. " +
                  "Returns user details and authentication tokens." +
                  "Access token is valid for 5 minutes, Refresh token - for 1 day")
       public ResponseEntity<Map<String, Object>> sign_up(@Valid @RequestBody UserRequestDto userRequestDto) {
           Map<String, Object> createdUser = userService.createUser(userRequestDto);
           return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/sign_in")
    @Operation(summary = "User login",
               description = "Authenticates user and returns JWT tokens." +
                       "Access token is valid for 10 minutes, Refresh token - for 1 day")
    public ResponseEntity<JwtAuthenticationDto> sign_in(@Valid@RequestBody UserCredentialDto userCredentialDto) throws AuthenticationException {
        JwtAuthenticationDto jwtAuthenticationDTO = userService.loginUser(userCredentialDto);
        return ResponseEntity.ok(jwtAuthenticationDTO);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Obtains a new access token using a valid refresh token")
    public JwtAuthenticationDto refresh (
            @RequestBody RefreshTokenDto refreshTokenDto
            ) throws  Exception {
      return userService.refreshToken(refreshTokenDto);
    }
}
