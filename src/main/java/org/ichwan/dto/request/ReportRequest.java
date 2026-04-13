package org.ichwan.dto.request;

public record ReportRequest(
        String content,
        String answer,
        String score,
        Long userId,
        Long categoryId,
        Long questionId
) {}
