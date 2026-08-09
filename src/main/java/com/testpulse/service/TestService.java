package com.testpulse.service;



import com.testpulse.model.Question;
import com.testpulse.model.Test;

import java.util.List;

public interface TestService {
    List<Test>  getAllTests(String searchQuery, String subject);
    Test  getTestById(Long id);
    Test createCustomTest(String subject, int questionCount, String difficulty, String mode);


    List<Test> addTest(List<Test> tests);
}