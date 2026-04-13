package org.ichwan.dto.response;

public record AuthResponse(String token, UserResponse user) {
}
