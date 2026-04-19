package org.ichwan.dto.request;

import org.ichwan.util.UserRole;

public record UserRequest(
        String name,
        String regnumber,
        String gender,
        UserRole role,
        String password,
        Long classRoom
) {}