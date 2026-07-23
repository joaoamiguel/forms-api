package com.example.iaassessment.service.impl;

import com.example.iaassessment.dto.FormDto;
import com.example.iaassessment.entity.FormEntity;
import com.example.iaassessment.entity.RoleName;
import com.example.iaassessment.entity.UserEntity;
import com.example.iaassessment.repository.FormRepository;
import com.example.iaassessment.repository.QuestionRepository;
import com.example.iaassessment.service.FormService;
import com.example.iaassessment.util.AuthenticatedUser;
import com.example.iaassessment.util.QuestionMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl implements FormService {
    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;
    public FormServiceImpl(FormRepository formRepository, QuestionRepository questionRepository) { this.formRepository = formRepository; this.questionRepository = questionRepository; }

    @Override
    public List<FormDto> availableForms() {
        UserEntity user = AuthenticatedUser.currentUser();
        Set<RoleName> roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        return formRepository.findAll().stream()
                .filter(FormEntity::isActive)
                .filter(form -> hasAccess(form, roles))
                .map(this::toDto)
                .toList();
    }

    @Override
    public FormDto getFormByCode(String code) {
        FormEntity form = formRepository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Formulário não encontrado"));
        UserEntity user = AuthenticatedUser.currentUser();
        Set<RoleName> roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        if (!hasAccess(form, roles)) throw new IllegalArgumentException("Você não possui acesso a este formulário");
        return toDto(form);
    }

    /**
     * Um formulário sem roles restritivas (allowedRoles vazio) é público a
     * qualquer usuário autenticado. Quando há roles definidas, o usuário
     * precisa possuir ao menos uma delas.
     */
    private boolean hasAccess(FormEntity form, Set<RoleName> userRoles) {
        return form.getAllowedRoles().isEmpty() || form.getAllowedRoles().stream().anyMatch(userRoles::contains);
    }

    private FormDto toDto(FormEntity form) {
        return new FormDto(form.getId(), form.getCode(), form.getTitle(), form.getDescription(), form.getAllowedRoles().stream().map(Enum::name).toList(), QuestionMapper.toDtos(questionRepository.findByFormOrderByDisplayOrderAscIdAsc(form)));
    }
}
