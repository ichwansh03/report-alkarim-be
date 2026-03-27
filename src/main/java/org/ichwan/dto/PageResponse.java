package org.ichwan.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        int page,
        int size,
        long totalItems,
        int totalPages
) {}
