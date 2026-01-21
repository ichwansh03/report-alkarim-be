package org.ichwan.dto;

import org.ichwan.domain.User;

public record AuthResponse(String regnumber, String token, User user) {
}
