package org.ichwan.dto;

import org.ichwan.util.UserRole;

public record UserRequest(
        String name,
        String clsroom,
        String gender,
        UserRole roles,
        String regnumber
) {}
