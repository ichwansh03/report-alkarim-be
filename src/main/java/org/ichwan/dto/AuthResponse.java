package org.ichwan.dto;

public record AuthResponse(String regnumber, String token, UserResponse user) {
}
