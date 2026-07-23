package com.example.iaassessment.service.impl;

import com.example.iaassessment.dto.RegisterRequest;
import com.example.iaassessment.entity.UserEntity;
import com.example.iaassessment.repository.RoleRepository;
import com.example.iaassessment.repository.UserRepository;
import com.example.iaassessment.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Test
    void selfRegistrationNeverGrantsAdminRole() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(anyString(), anyMap())).thenReturn("token");

        AuthServiceImpl service = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, authenticationManager, jwtService);
        var response = service.register(new RegisterRequest("Colaborador", "colaborador@test.com", "123456"));

        assertTrue(response.roles().isEmpty());
        verifyNoInteractions(roleRepository);
    }

    @Test
    void rejectsDuplicateEmail() {
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        AuthServiceImpl service = new AuthServiceImpl(userRepository, roleRepository, passwordEncoder, authenticationManager, jwtService);
        assertThrows(IllegalArgumentException.class, () -> service.register(new RegisterRequest("Nome", "existente@test.com", "123456")));
    }
}
