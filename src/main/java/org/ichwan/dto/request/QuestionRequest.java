package org.ichwan.dto.request;

import org.ichwan.util.AnswerType;

public record QuestionRequest(
        String question,
        String options,
        Long classRoom,
        Long category
) {}
