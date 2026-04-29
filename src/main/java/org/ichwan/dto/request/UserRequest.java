package org.ichwan.dto.request;

public record UserRequest(
        String name,
        String regnumber,
        String gender,
        String roles,
        String password,
        Long classRoom
) {}