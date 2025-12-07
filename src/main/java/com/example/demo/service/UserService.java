package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.mapping.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AdminService adminService;

    @Transactional
    public Map<String, Object> createUser(UserRequestDto userRequestDto) {

        if(userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new UserAlreadyExistsException(
                    String.format("User with email %s already exists", userRequestDto.getEmail()));
        }

        if (userRequestDto.getPassword() == null ) {
            throw new IllegalStateException( "Password not be empty" );
        }

        UserEntity userEntity = mapper.maptoUserEntity(userRequestDto);
        userEntity.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        userEntity.setUserStatus(UserStatus.ACTIVE);

        Set<RoleEntity> userRoles = new HashSet<>();

        RoleEntity userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> createRole("ROLE_USER"));
        userRoles.add(userRole);

        if (adminService.isAdminEmail(userRequestDto.getEmail())) {
            RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> createRole("ROLE_ADMIN"));
            userRoles.add(adminRole);
            log.info("User {} assigned ADMIN role", userRequestDto.getEmail());
        }

        userEntity.setRoles(userRoles);
        var savedEntity = userRepository.save(userEntity);
        log.info("User created successfully with id: {}", savedEntity.getId());

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(savedEntity.getEmail());
        log.info("Creating new user with tokens for email: {}", userRequestDto.getEmail());

        UserResponseDto userResponseDto = mapper.mapToUserDto(savedEntity);
        Map<String, Object> response = new HashMap<>();
        response.put("user", userResponseDto);
        response.put("tokens", tokens);

        return response;
    }

    public JwtAuthenticationDto loginUser(UserCredentialDto userCredentialDto) throws AuthenticationException{
      UserEntity userEntity = findByCredential(userCredentialDto);
      return jwtService.generateAuthToken(userEntity.getEmail());
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception{
        String refreshToken = refreshTokenDto.getRefreshToken();
        if(refreshToken !=null && jwtService.validateToken(refreshToken)){
            UserEntity user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    private UserEntity findByCredential(UserCredentialDto userCredentialDto) throws AuthenticationException {
        Optional<UserEntity> user = userRepository.findByEmail(userCredentialDto.getEmail());
        if (user.isPresent()){
            UserEntity userEntity =  user.get();
            if(passwordEncoder.matches(userCredentialDto.getPassword(), userEntity.getPassword())) {
               return userEntity;
            }
        }
        throw new AuthenticationException("Email or password is not correct");
    }

    private RoleEntity createRole(String roleName) {
        RoleEntity role = new RoleEntity();
        role.setName(roleName);
        log.info("Creating new role: {}", roleName);
        return roleRepository.save(role);
    }

    private UserEntity findByEmail(String email) throws Exception {
        return  userRepository.findByEmail(email)
                .orElseThrow(()-> new ExpressionException(String.format("User with email %s is not found", email)));
    }
}

