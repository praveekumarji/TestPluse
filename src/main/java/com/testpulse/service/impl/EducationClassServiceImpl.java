package com.testpulse.service.impl;

import com.testpulse.dto.CreateEducationClassRequest;
import com.testpulse.model.EducationClass;
import com.testpulse.repository.EducationClassRepository;
import com.testpulse.service.EducationClassService;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class EducationClassServiceImpl implements EducationClassService {

    private final EducationClassRepository educationClassRepository;

    public EducationClassServiceImpl(EducationClassRepository educationClassRepository) {
        this.educationClassRepository = educationClassRepository;
    }

    @Override
    @Cacheable(value = "educationClasses", key = "'active'")
    public List<EducationClass> getActiveClasses() {
        return educationClassRepository.findByActiveTrueOrderByNameAsc();
    }

    @Override
    @Cacheable(value = "educationClasses", key = "#id")
    public EducationClass getActiveClassById(Long id) {
        return educationClassRepository.findById(id)
                .filter(EducationClass::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Class not found."));
    }

    @Override
    public EducationClass createClass(CreateEducationClassRequest request) {
        if (request == null || request.getCode() == null || request.getCode().isBlank()
                || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Class code and name are required.");
        }

        String code = request.getCode().trim().toUpperCase(java.util.Locale.ROOT);
        String name = request.getName().trim();
        if (educationClassRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("Class code already exists.");
        }
        if (educationClassRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Class name already exists.");
        }

        return educationClassRepository.save(EducationClass.builder()
                .code(code)
                .name(name)
                .active(true)
                .build());
    }
}