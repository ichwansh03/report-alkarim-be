package org.ichwan.dto;

import org.ichwan.domain.User;

public record ReportResponse(String category, String content, String regnumber, String marked, User user) {
}
