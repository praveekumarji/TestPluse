package com.testpulse.service;

import com.testpulse.model.EducationClass;
import com.testpulse.dto.CreateEducationClassRequest;

import java.util.List;

public interface EducationClassService {
    List<EducationClass> getActiveClasses();

    EducationClass getActiveClassById(Long id);

    EducationClass createClass(CreateEducationClassRequest request);
}