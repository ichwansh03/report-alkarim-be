package org.ichwan.service;


import org.ichwan.dto.request.QuestionRequest;
import org.ichwan.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService extends BaseService<QuestionRequest, QuestionResponse> {

    List<QuestionResponse> getQuestionByTarget(String target);

    List<QuestionResponse> getQuestionByCategory(String category);

    List<QuestionResponse> getQuestionByCategoryAndTarget(String category, String target);

}
