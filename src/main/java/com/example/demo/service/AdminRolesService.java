package com.example.demo.service;

import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapping.UserMapper;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdmineServiceRoles {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public List<UserResponseDto> getAllUsers() {
        List<UserEntity> result = userRepository.findAll();
        log.info("{} users found", result.size());

        return result.stream()
                .map(mapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto findUserById(Long id ) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found user by id = " + id
                ));

        var userDTOResponse = mapper.mapToUserDto(userEntity);
        log.info("User with id{} is found {} ", id, userDTOResponse);
        return userDTOResponse;
    }

}
