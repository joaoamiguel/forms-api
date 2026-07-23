package com.example.iaassessment.dto;

import java.util.List;

public record AuthResponse(String token, Long userId, String name, String email, List<String> roles) {}
