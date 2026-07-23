package com.example.iaassessment.util;

import com.example.iaassessment.entity.UserEntity;
import com.example.iaassessment.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedUser {
    public static UserEntity currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails cud)) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        return cud.getUser();
    }
}
