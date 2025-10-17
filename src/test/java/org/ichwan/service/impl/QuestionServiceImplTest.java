package org.ichwan.service.impl;

import org.ichwan.domain.Question;
import org.ichwan.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceImplTest {
    @Mock
    QuestionRepository questionRepository;
    @InjectMocks
    QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateQuestionThrowsIfExists() {
        Question question = new Question();
        question.setId(1L);
        when(questionRepository.findById(1L)).thenReturn(question);
        assertThrows(IllegalArgumentException.class, () -> questionService.createQuestion(question));
    }

    @Test
    void testGetQuestionById() {
        Question question = new Question();
        when(questionRepository.findById(1L)).thenReturn(question);
        assertNotNull(questionService.getQuestionById(1L));
    }

    @Test
    void testGetQuestionByTarget() {
        when(questionRepository.findQuestionByTarget("target")).thenReturn(Collections.emptyList());
        List<Question> questions = questionService.getQuestionByTarget("target");
        assertNotNull(questions);
    }

    @Test
    void testUpdateQuestionThrowsIfNotFound() {
        when(questionRepository.findById(1L)).thenReturn(null);
        Question question = new Question();
        assertThrows(IllegalArgumentException.class, () -> questionService.updateQuestion(question, 1L));
    }

    @Test
    void testDeleteQuestionThrowsIfNotFound() {
        when(questionRepository.findById(1L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> questionService.deleteQuestion(1L));
    }
}

