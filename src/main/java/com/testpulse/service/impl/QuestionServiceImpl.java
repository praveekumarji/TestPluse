package com.testpulse.service.impl;

import com.testpulse.dto.CreateQuestionRequest;
import com.testpulse.model.Question;
import com.testpulse.model.Test;
import com.testpulse.repository.QuestionRepository;
import com.testpulse.repository.TestRepository;
import com.testpulse.service.QuestionService;
import com.testpulse.util.LocalizedTextResolver;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, TestRepository testRepository) {
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    @Override
    public List<Question> getQuestionsByTestId(Long testId, String lang) {
        List<Question> questions = questionRepository.findByTest_Id(testId);
        return questions.stream().map(question -> applyLanguage(question, lang)).toList();
    }

    @Override
    public List<Question> createQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("At least one question is required.");
        }

        for (Question question : questions) {
            validateQuestion(question);
            resolveTestReference(question);
        }

        return questionRepository.saveAll(questions);
    }

    @Override
    public List<Question> createQuestionsFromDto(List<CreateQuestionRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("At least one question is required.");
        }

        List<Question> questions = new java.util.ArrayList<>();
        for (CreateQuestionRequest request : requests) {
            questions.add(mapToEntity(request));
        }
        return createQuestions(questions);
    }

    @Override
    public List<Question> importQuestionsFromExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Excel file is required.");
        }

        String originalName = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase(Locale.ROOT);
        if (!originalName.endsWith(".xlsx") && !originalName.endsWith(".xls")) {
            throw new IllegalArgumentException("Only Excel files (.xlsx, .xls) are supported.");
        }

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2) {
                throw new IllegalArgumentException("Excel file must contain a header row and at least one question row.");
            }

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = mapHeaders(headerRow);
            if (columnMap.isEmpty()) {
                throw new IllegalArgumentException("Excel header row is missing or unreadable.");
            }

            List<Question> questions = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                questions.add(parseQuestionRow(row, columnMap));
            }

            if (questions.isEmpty()) {
                throw new IllegalArgumentException("No valid question rows were found in the uploaded Excel file.");
            }

            return createQuestions(questions);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read Excel file: " + ex.getMessage(), ex);
        }
    }

    private Question parseQuestionRow(Row row, Map<String, Integer> columnMap) {
        Long testId = readLong(row, columnMap, "testid");
        if (testId == null) {
            throw new IllegalArgumentException("Each row must include a valid testId column.");
        }

        String subject = readString(row, columnMap, "subject");
        String subjectHi = readString(row, columnMap, "subjecthi");
        String text = readString(row, columnMap, "text");
        String textHi = readString(row, columnMap, "texthi");
        String explanation = readString(row, columnMap, "explanation");
        String explanationHi = readString(row, columnMap, "explanationhi");
        String hint = readString(row, columnMap, "hint");
        String topic = readString(row, columnMap, "topic");

        List<String> options = readOptions(row, columnMap, "option");
        List<String> optionsHi = readOptions(row, columnMap, "optionhi");

        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Each question row must have at least two option columns (option1, option2, ...). ");
        }

        Integer correctIndex = readInteger(row, columnMap, "correctoptionindex");
        if (correctIndex == null) {
            correctIndex = readInteger(row, columnMap, "correctoption");
        }
        if (correctIndex == null) {
            throw new IllegalArgumentException("Each question row must include correctOptionIndex.");
        }
        if (correctIndex < 0 || correctIndex >= options.size()) {
            throw new IllegalArgumentException("correctOptionIndex is out of range for row with testId=" + testId);
        }

        Question question = Question.builder()
                .subject(subject)
                .subjectHi(subjectHi)
                .topic(topic)
                .text(text)
                .textHi(textHi)
                .options(options)
                .optionsHi(optionsHi.isEmpty() ? null : optionsHi)
                .correctOptionIndex(correctIndex)
                .explanation(explanation)
                .explanationHi(explanationHi)
                .hint(hint)
                .build();

        question.setTestId(testId);
        return question;
    }

    private Map<String, Integer> mapHeaders(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int colIndex = 0; colIndex <= headerRow.getLastCellNum(); colIndex++) {
            Cell cell = headerRow.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                continue;
            }
            String header = normalizeHeader(cell.toString());
            if (!header.isBlank()) {
                headerMap.put(header, colIndex);
            }
        }
        return headerMap;
    }

    private String normalizeHeader(String value) {
        return value == null ? "" : value.trim().replace(" ", "").replace("_", "").toLowerCase(Locale.ROOT);
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i <= row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String readString(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer index = columnMap.get(columnName);
        if (index == null) {
            return null;
        }
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? null : cell.toString().trim();
    }

    private Long readLong(Row row, Map<String, Integer> columnMap, String columnName) {
        String value = readString(row, columnMap, columnName);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Column '" + columnName + "' must contain a valid number.");
        }
    }

    private Integer readInteger(Row row, Map<String, Integer> columnMap, String columnName) {
        String value = readString(row, columnMap, columnName);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Column '" + columnName + "' must contain a valid integer.");
        }
    }

    private List<String> readOptions(Row row, Map<String, Integer> columnMap, String prefix) {
        List<String> values = new ArrayList<>();
        int optionIndex = 1;
        while (true) {
            String key = prefix + optionIndex;
            Integer col = columnMap.get(key);
            if (col == null) {
                break;
            }
            Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String value = cell == null ? null : cell.toString().trim();
            if (value != null && !value.isEmpty()) {
                values.add(value);
            }
            optionIndex++;
        }
        return values;
    }

    private Question mapToEntity(CreateQuestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Question payload cannot be null.");
        }
        if (request.getTestId() == null) {
            throw new IllegalArgumentException("Question testId is required.");
        }
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new IllegalArgumentException("Question subject is required.");
        }
        if (request.getText() == null || request.getText().isBlank()) {
            throw new IllegalArgumentException("Question text is required.");
        }
        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new IllegalArgumentException("Each question must include at least two options.");
        }
        if (request.getCorrectOptionIndex() < 0 || request.getCorrectOptionIndex() >= request.getOptions().size()) {
            throw new IllegalArgumentException("Correct option index is out of range.");
        }
        if (request.getOptionsHi() != null && !request.getOptionsHi().isEmpty() && request.getOptionsHi().size() != request.getOptions().size()) {
            throw new IllegalArgumentException("Hindi options count must match English options count.");
        }

        Question question = Question.builder()
                .subject(request.getSubject())
                .subjectHi(request.getSubjectHi())
                .text(request.getText())
                .textHi(request.getTextHi())
                .options(request.getOptions())
                .optionsHi(request.getOptionsHi())
                .correctOptionIndex(request.getCorrectOptionIndex())
                .explanation(request.getExplanation())
                .explanationHi(request.getExplanationHi())
                .hint(request.getHint())
                .build();

        question.setTestId(request.getTestId());
        return question;
    }

    private void validateQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question payload cannot be null.");
        }
        if (question.getTest() == null && (question.getTestId() == null || question.getTestId().isBlank())) {
            throw new IllegalArgumentException("Question testId is required.");
        }
        if (question.getSubject() == null || question.getSubject().isBlank()) {
            throw new IllegalArgumentException("Question subject is required.");
        }
        if (question.getText() == null || question.getText().isBlank()) {
            throw new IllegalArgumentException("Question text is required.");
        }
        if (question.getOptions() == null || question.getOptions().size() < 2) {
            throw new IllegalArgumentException("Each question must include at least two options.");
        }
        if (question.getCorrectOptionIndex() < 0 || question.getCorrectOptionIndex() >= question.getOptions().size()) {
            throw new IllegalArgumentException("Correct option index is out of range.");
        }
        if (question.getOptionsHi() != null && !question.getOptionsHi().isEmpty() && question.getOptionsHi().size() != question.getOptions().size()) {
            throw new IllegalArgumentException("Hindi options count must match English options count.");
        }
    }

    private void resolveTestReference(Question question) {
        if (question.getTest() != null) {
            return;
        }

        String testId = question.getTestId();
        if (testId == null || testId.isBlank()) {
            throw new IllegalArgumentException("Question testId is required.");
        }

        Test test = testRepository.findById(Long.valueOf(testId))
                .orElseThrow(() -> new IllegalArgumentException("Test not found for id: " + testId));
        question.setTest(test);
    }

    private Question applyLanguage(Question question, String lang) {
        if (question == null) {
            return null;
        }

        question.setText(LocalizedTextResolver.resolve(question.getText(), question.getTextHi(), lang));
        question.setSubject(LocalizedTextResolver.resolve(question.getSubject(), question.getSubjectHi(), lang));
        question.setExplanation(LocalizedTextResolver.resolve(question.getExplanation(), question.getExplanationHi(), lang));
        question.setOptions(LocalizedTextResolver.resolveList(question.getOptions(), question.getOptionsHi(), lang));
        return question;
    }
}
