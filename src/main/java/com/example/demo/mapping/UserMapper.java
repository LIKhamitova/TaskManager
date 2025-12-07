package com.example.demo.mapping;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    //из entity в dto
    public UserResponseDto mapToUserDto(UserEntity userEntity) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userEntity.getId());
        dto.setName(userEntity.getName());
        dto.setEmail(userEntity.getEmail());
        dto.setUserStatus(userEntity.getUserStatus());
        if (userEntity.getRoles() != null && !userEntity.getRoles().isEmpty()) {
            List<String> roleNames = userEntity.getRoles().stream()
                    .map(RoleEntity::getName)
                    .toList();
            dto.setRoles(roleNames);
        }
        dto.setCreatedAt(userEntity.getCreatedAt());
        dto.setUpdatedAt(userEntity.getUpdatedAt());
        return dto;
    }
    //из dto в entity
    public UserEntity maptoUserEntity(UserRequestDto userRequestDto) {
        UserEntity entity = new UserEntity();
        entity.setName(userRequestDto.getName());
        entity.setEmail(userRequestDto.getEmail());
        entity.setPassword(userRequestDto.getPassword());
        return entity;
    }

    //response в entity
    public UserEntity mapToResponseEntity(UserResponseDto userResponse){
        UserEntity entity = new UserEntity();
        entity.setId(userResponse.getId());
        entity.setName(userResponse.getName());
        entity.setEmail(userResponse.getEmail());
        entity.setUserStatus(userResponse.getUserStatus());

        return entity;
    }

}
