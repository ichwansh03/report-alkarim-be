package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Question;
import org.ichwan.dto.request.QuestionRequest;
import org.ichwan.dto.response.PageResponse;
import org.ichwan.dto.response.QuestionResponse;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.CategoryRepository;
import org.ichwan.repository.ClassRoomRepository;
import org.ichwan.repository.QuestionRepository;
import org.ichwan.service.QuestionService;
import org.ichwan.util.MapperConfig;

import java.util.List;

@ApplicationScoped
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Inject
    private QuestionRepository repository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private ClassRoomRepository classRoomRepository;

    @Inject
    private MapperConfig mapper;

    @Override
    @Transactional
    public void createQuestion(QuestionRequest entity) {
        // Use content check instead of ID check, since ID is auto-generated
        boolean exists = repository.find("question", entity.question()).firstResultOptional().isPresent();
        if (exists) {
            throw new ConflictException("Question '" + entity.question() + "' already exists");
        }

        Question question = new Question();
        question.setQuestion(entity.question());
        question.setOptions(entity.options());
        question.setCategory(categoryRepository.findById(entity.categoryId()));
        question.setClassRoom(classRoomRepository.findById(entity.classRoomId()));
        repository.persistAndFlush(question);
    }

    @CacheResult(cacheName = "questions", lockTimeout = 3000)
    @Override
    public List<QuestionResponse> getQuestionByTarget(String target) {
        List<Question> questions = repository.findQuestionByTarget(target);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for target '" + target + "'");
        }
        return mapper.mapList(questions, QuestionResponse.class);
    }

    @CacheResult(cacheName = "questions", lockTimeout = 3000)
    @Override
    public List<QuestionResponse> getQuestionByCategory(String category) {
        List<Question> questions = repository.findQuestionByCategory(category);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for category '" + category + "'");
        }
        return mapper.mapList(questions, QuestionResponse.class);
    }

    @CacheResult(cacheName = "questions", lockTimeout = 3000)
    @Override
    public List<QuestionResponse> getQuestionByCategoryAndTarget(String category, String target) {
        List<Question> questions = repository.findQuestionByCategoryAndTarget(category, target);
        if (questions.isEmpty()) {
            throw new NotFoundException("No questions found for category '" + category + "' and target '" + target + "'");
        }
        return mapper.mapList(questions, QuestionResponse.class);
    }

    @Override
    @Transactional
    public void updateQuestion(QuestionRequest entity, Long id) {
        Question question = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Question with id " + id + " not found"));

        // Check if the new question content conflicts with another existing question
        repository.find("question = ?1 and id != ?2", entity.question(), id)
                .firstResultOptional()
                .ifPresent(q -> {
                    throw new ConflictException("Question '" + entity.question() + "' already exists");
                });

        question.setQuestion(entity.question());
        question.setOptions(entity.options());
        question.setCategory(categoryRepository.findById(entity.categoryId()));
        question.setClassRoom(classRoomRepository.findById(entity.classRoomId()));
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

    @Override
    public QuestionResponse findById(Long id) {
        Question question = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Question with id " + id + " not found"));
        return mapper.map(question, QuestionResponse.class);
    }

    @Override
    public PageResponse<QuestionResponse> getAll(int page, int size) {
        PanacheQuery<Question> query = repository.findAll();
        query.page(Page.of(page, size));

        List<Question> questions = query.list();
        if (questions.isEmpty()) {
            throw new NotFoundException("No users found");
        }

        long totalItems = query.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<QuestionResponse> data = mapper.mapList(questions, QuestionResponse.class);

        return new PageResponse<>(data, page, size, totalItems, totalPages);
    }
}
