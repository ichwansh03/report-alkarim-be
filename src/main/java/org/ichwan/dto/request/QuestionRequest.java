package org.ichwan.dto.request;

import org.ichwan.util.AnswerType;

public record QuestionRequest(
        String question,
        AnswerType options,
        Long categoryId,
        Long classRoomId
) {}
