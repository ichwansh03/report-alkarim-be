package org.ichwan.service;

import org.ichwan.dto.response.PageResponse;

public interface BaseService<R,E> {
    E findById(Long id);

    PageResponse<E> getAll(int page, int size);

    E create(R req);

    E update(R req, Long id);

    void delete(Long id);
}
