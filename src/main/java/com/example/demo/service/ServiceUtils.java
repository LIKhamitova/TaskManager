package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.exception.UserNotAuthenticatedException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.GetEmailFromSecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckAuthUserService {
    private final GetEmailFromSecurityContext getEmailFromSecurityContext;
    private final UserRepository userRepository;

    public UserEntity checkAuthUser() {
        String userEmail = getEmailFromSecurityContext.getEmailFromSecurityContext();

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotAuthenticatedException("User not authenticated or invalid token: " + userEmail));
    }
}
