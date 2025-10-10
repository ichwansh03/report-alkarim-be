package org.ichwan.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Question;
import org.ichwan.repository.QuestionRepository;
import org.ichwan.service.QuestionService;

import java.util.List;

@ApplicationScoped
@Transactional
public class QuestionServiceImpl implements QuestionService<Question> {

    @Inject
    private QuestionRepository repository;

    @Override
    public void createQuestion(Question entity) {
        if (getQuestionById(entity.getId()) != null) {
            throw new IllegalArgumentException("question already exists");
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
        return repository.findById(id);
    }

    @Override
    public List<Question> getQuestionByTarget(String target) {
        return repository.getQuestionByTarget(target);
    }

    @Override
    public void updateQuestion(Question entity, Long id) {
        Question question = repository.findById(id);
        if (question == null) {
            throw new IllegalArgumentException("question not found");
        } else {
            question.setQuestion(entity.getQuestion());
            question.setOptions(entity.getOptions());
            question.setCategory(entity.getCategory());
            question.setTarget(entity.getTarget());
            repository.persist(question);
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        if (repository.findById(id) != null) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("question not found");
        }
    }
}
