package org.ichwan.dto;

import org.ichwan.util.UserRole;

public record AuthRequest(String name, String clsroom, String gender, UserRole roles, String regnumber, String password) {
}
