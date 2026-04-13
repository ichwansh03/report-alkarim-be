package org.ichwan.service;

import org.ichwan.dto.response.PageResponse;

public interface BaseService<E> {
    E findById(Long id);

    PageResponse<E> getAll(int page, int size);
}
