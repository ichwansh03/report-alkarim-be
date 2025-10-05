package org.ichwan.dto;

public record ReportRequest(String category, String content, String regnumber, String marked, Boolean action) {
}
