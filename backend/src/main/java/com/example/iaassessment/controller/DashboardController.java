package com.example.iaassessment.controller;

import com.example.iaassessment.dto.DashboardDto;
import com.example.iaassessment.service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }

    @GetMapping("/dashboard")
    public DashboardDto dashboard() { return dashboardService.dashboard(); }
}
