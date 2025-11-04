package org.ichwan.dto;

import org.ichwan.util.UserRole;

import java.time.LocalDateTime;

public record UserResponse(String name, String clsroom, String gender, UserRole roles, String regnumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
