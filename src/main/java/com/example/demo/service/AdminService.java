package com.example.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/*
 * Сервис для управления администраторами.
 * Роль ADMIN присваивается при нахождении email пользователя
 * в переменной окружения app.admin.emails.
 * Email перечисляются через запятую.
 * Email НЕ чувствителен к регистру (приводятся к нижнему).
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    @Value("${app.admin.emails:}")
    private String adminEmailEnv;

    private final Set<String> adminEmails = new HashSet<>();

    @PostConstruct
    public void initAdminEmails() {
        if (adminEmailEnv != null && !adminEmailEnv.trim().isEmpty()) {
            String[] emailsArray = adminEmailEnv.split(",");

            for (String email : emailsArray) {
                if (email != null ) {
                    String emailNoSpace  = email.trim();
                    if(!emailNoSpace.isEmpty()) {
                        adminEmails.add(emailNoSpace.toLowerCase());
                    }
                }
            }
            if (!adminEmails.isEmpty()) {
                log.info("Admin emails are loaded: {}", adminEmails);
            }else {
                log.error("Errors during admin list creation");
            }

        } else {
            log.warn("Admin emails are not specified in the environment variable");
            log.info("Set: app.admin.emails");
        }
    }
    public boolean isAdminEmail(String email) {
        if( email == null) {
            return  false;
        }
        return adminEmails.contains(email.toLowerCase().trim());
    }
}
