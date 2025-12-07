package com.example.demo.security;

import com.example.demo.entity.UserEntity;
import com.example.demo.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
/*
Пояснения:
 - UserCredentialDto -  используется email и пароль
 - isAccountNonExpired - логика действия со сроком действия аккаунта не реализовала(всегда - true)
 - isAccountNonLocked и isEnabled  - логика по блокировке пользователей не реализована(всегда - ACTIVE)
*/

public record CustomUserDetails( UserEntity userEntity ) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
               .toList();
    }
    @Override
    public String getUsername() { //  ! пояснение в шапке
        return userEntity.getEmail();
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired(); //  ! пояснение в шапке
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getUserStatus() != UserStatus.BLOCKED; //  ! пояснение в шапке
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getUserStatus() == UserStatus.ACTIVE;  //  ! пояснение в шапке
    }
}
