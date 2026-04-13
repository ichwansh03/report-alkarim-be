package org.ichwan.dto.request;

public record ClassRoomRequest(
        String name,
        Long levelId,
        Long teacherId
) {}
