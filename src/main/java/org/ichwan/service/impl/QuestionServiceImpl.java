package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Question;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.QuestionRepository;
import org.ichwan.service.QuestionService;

import java.util.List;

@ApplicationScoped
@Transactional
public class QuestionServiceImpl implements QuestionService<Question> {

    @Inject
    private QuestionRepository repository;

    @Override
    @Transactional
    public void createQuestion(Question entity) {
        // Use content check instead of ID check, since ID is auto-generated
        boolean exists = repository.find("question", entity.getQuestion()).firstResultOptional().isPresent();
        if (exists) {
            throw new ConflictException("Question '" + entity.getQuestion() + "' already exists");
        }

        Question question = new Question();
        question.setQuestion(entity.getQuestion());
        question.setOptions(entity.getOptions());
        question.setCategory(entity.getCategory());
        question.setTarget(entity.getTarget());
        repository.persistAndFlush(question);
    }

    @Override
    public Question getQuestionById(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Question with id " + id + " not found"));
    }

    @CacheResult(cacheName = "questions", lockTimeout = 3000)
    @Override
    public List<Question> getQuestionByTarget(String target) {
        List<Question> questions = repository.findQuestionByTarget(target);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for target '" + target + "'");
        }
        return questions;
    }

    @CacheResult(cacheName = "questions", lockTimeout = 3000)
    @Override
    public List<Question> getQuestionByCategory(String category) {
        List<Question> questions = repository.findQuestionByCategory(category);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for category '" + category + "'");
        }
        return questions;
    }

    @CacheResult(cacheName = "questions", lockTimeout = 3000)
    @Override
    public List<Question> getQuestionByCategoryAndTarget(String category, String target) {
        List<Question> questions = repository.findQuestionByCategoryAndTarget(category, target);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for category '" + category + "' and target '" + target + "'");
        }
        return questions;
    }

    @Override
    @Transactional
    public void updateQuestion(Question entity, Long id) {
        Question question = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Question with id " + id + " not found"));

        // Check if the new question content conflicts with another existing question
        repository.find("question = ?1 and id != ?2", entity.getQuestion(), id)
                .firstResultOptional()
                .ifPresent(q -> {
                    throw new ConflictException("Question '" + entity.getQuestion() + "' already exists");
                });

        question.setQuestion(entity.getQuestion());
        question.setOptions(entity.getOptions());
        question.setCategory(entity.getCategory());
        question.setTarget(entity.getTarget());
        repository.persist(question);
    }

    @CacheInvalidate(cacheName = "questions")
    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Question with id " + id + " not found"));

        repository.deleteById(id);
    }
}
