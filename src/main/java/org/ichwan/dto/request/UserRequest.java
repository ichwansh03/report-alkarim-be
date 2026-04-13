package org.ichwan.dto.request;

import org.ichwan.util.UserRole;

public record UserRequest(
        String name,
        String regNumber,
        String gender,
        UserRole role,
        String password,
        Long classRoomId
) {}