package org.ichwan.dto;

public record ReportRequest(String category, String content, Long userId, String score, String answer) {
}
