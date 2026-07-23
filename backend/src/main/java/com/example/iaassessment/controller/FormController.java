package com.example.iaassessment.controller;

import com.example.iaassessment.dto.FormDto;
import com.example.iaassessment.service.FormService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
@CrossOrigin(origins = "*")
public class FormController {
    private final FormService formService;
    public FormController(FormService formService) { this.formService = formService; }

    @GetMapping
    public List<FormDto> list() { return formService.availableForms(); }

    @GetMapping("/{code}")
    public FormDto byCode(@PathVariable String code) { return formService.getFormByCode(code); }
}
