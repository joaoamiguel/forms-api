package com.example.iaassessment.controller;

import com.example.iaassessment.dto.SubmissionRequest;
import com.example.iaassessment.dto.SubmissionResponse;
import com.example.iaassessment.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin(origins = "*")
public class SubmissionController {
    private final SubmissionService submissionService;
    public SubmissionController(SubmissionService submissionService) { this.submissionService = submissionService; }

    @PostMapping("/{formCode}")
    public SubmissionResponse submit(@PathVariable String formCode, @Valid @RequestBody SubmissionRequest request) {
        return submissionService.submit(formCode, request);
    }

    @GetMapping("/{formCode}/mine")
    public SubmissionResponse mine(@PathVariable String formCode) { return submissionService.latestMine(formCode); }
}
