package com.example.iaassessment.service.impl;

import com.example.iaassessment.dto.*;
import com.example.iaassessment.entity.*;
import com.example.iaassessment.repository.RoleRepository;
import com.example.iaassessment.repository.UserRepository;
import com.example.iaassessment.security.CustomUserDetails;
import com.example.iaassessment.security.JwtService;
import com.example.iaassessment.service.AuthService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository; this.roleRepository = roleRepository; this.passwordEncoder = passwordEncoder; this.authenticationManager = authenticationManager; this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) throw new IllegalArgumentException("Email já cadastrado");
        // A role ADMIN nunca é atribuída via autocadastro; usuários novos nascem
        // sem nenhuma role, com acesso apenas ao Formulário 1.
        UserEntity user = new UserEntity(); user.setName(request.name()); user.setEmail(request.email()); user.setPassword(passwordEncoder.encode(request.password()));
        UserEntity saved = userRepository.save(user);
        return issueToken(saved);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserEntity user = userRepository.findByEmail(request.email()).orElseThrow();
        return issueToken(user);
    }

    @Override
    public MeResponse me() {
        var user = ((CustomUserDetails) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles().stream().map(r -> r.getName().name()).toList());
    }

    private AuthResponse issueToken(UserEntity user) {
        List<String> roles = user.getRoles().stream().map(r -> r.getName().name()).toList();
        String token = jwtService.generateToken(user.getEmail(), Map.of("uid", user.getId(), "roles", roles));
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), roles);
    }
}
