package org.ichwan.service;


import java.util.List;

public interface QuestionService<E> {

    void createQuestion(E entity);

    E getQuestionById(Long id);

    List<E> getQuestionByTarget(String target);

    List<E> getQuestionByCategory(String category);

    List<E> getQuestionByCategoryAndTarget(String category, String target);

    void updateQuestion(E entity, Long id);

    void deleteQuestion(Long id);
}
