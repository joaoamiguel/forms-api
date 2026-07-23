package com.example.iaassessment.service;

import com.example.iaassessment.dto.FormDto;
import java.util.List;

public interface FormService {
    List<FormDto> availableForms();
    FormDto getFormByCode(String code);
}
