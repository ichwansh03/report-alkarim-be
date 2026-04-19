package org.ichwan.dto.request;

public record ClassRoomRequest(
        String name,
        Long level,
        Long teacher
) {}
