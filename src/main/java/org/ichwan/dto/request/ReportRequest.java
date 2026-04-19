package org.ichwan.dto.request;

public record ReportRequest(
        String content,
        String answer,
        String score,
        Long user,
        Long category,
        Long question
) {}
