package com.testpulse.service;

import com.testpulse.model.Question;
import com.testpulse.repository.QuestionRepository;
import com.testpulse.repository.TestRepository;
import com.testpulse.service.impl.QuestionServiceImpl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceExcelImportTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TestRepository testRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Test
    void shouldImportQuestionsFromExcelFile() throws Exception {
        com.testpulse.model.Test test = new com.testpulse.model.Test();
        test.setId(42L);
        when(testRepository.findById(42L)).thenReturn(Optional.of(test));
        when(questionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "questions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buildWorkbookBytes()
        );

        List<Question> imported = questionService.importQuestionsFromExcel(file);

        assertEquals(1, imported.size());
        assertEquals("Math", imported.get(0).getSubject());
        assertEquals("What is 2 + 2?", imported.get(0).getText());
        assertEquals(List.of("3", "4", "5", "6"), imported.get(0).getOptions());
        assertEquals(1, imported.get(0).getCorrectOptionIndex());
        assertEquals(42L, imported.get(0).getTest().getId());
    }

    private byte[] buildWorkbookBytes() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Questions");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("testId");
            header.createCell(1).setCellValue("subject");
            header.createCell(2).setCellValue("text");
            header.createCell(3).setCellValue("option1");
            header.createCell(4).setCellValue("option2");
            header.createCell(5).setCellValue("option3");
            header.createCell(6).setCellValue("option4");
            header.createCell(7).setCellValue("correctOptionIndex");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(42);
            row.createCell(1).setCellValue("Math");
            row.createCell(2).setCellValue("What is 2 + 2?");
            row.createCell(3).setCellValue("3");
            row.createCell(4).setCellValue("4");
            row.createCell(5).setCellValue("5");
            row.createCell(6).setCellValue("6");
            row.createCell(7).setCellValue(1);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
