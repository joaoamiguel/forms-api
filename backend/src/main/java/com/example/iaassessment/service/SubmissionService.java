package com.example.iaassessment.service;

import com.example.iaassessment.dto.SubmissionRequest;
import com.example.iaassessment.dto.SubmissionResponse;

public interface SubmissionService {
    SubmissionResponse submit(String formCode, SubmissionRequest request);
    SubmissionResponse latestMine(String formCode);
}
