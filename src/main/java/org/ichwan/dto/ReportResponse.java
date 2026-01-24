package org.ichwan.dto;

public record ReportResponse(String category, String content, String regnumber, String marked, UserResponse user) {
}
