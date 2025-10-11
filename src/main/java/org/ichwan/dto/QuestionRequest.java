package org.ichwan.dto;

import org.ichwan.util.AnswerType;

public record QuestionRequest(String question, String category, String target, AnswerType options) {
}
