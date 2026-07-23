package com.example.iaassessment.service;

import com.example.iaassessment.dto.AuthRequest;
import com.example.iaassessment.dto.AuthResponse;
import com.example.iaassessment.dto.MeResponse;
import com.example.iaassessment.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    MeResponse me();
}
