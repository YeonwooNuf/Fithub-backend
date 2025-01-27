package com.example.musinsabackend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BeanCheck {

    private final PasswordEncoder passwordEncoder;

    public BeanCheck(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void checkBean() {
        if (passwordEncoder != null) {
            System.out.println("BCryptPasswordEncoder bean is registered successfully.");
        } else {
            throw new IllegalStateException("BCryptPasswordEncoder bean is not registered.");
        }
    }
}
