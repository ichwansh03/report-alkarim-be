package org.ichwan.service;

import org.ichwan.dto.response.PageResponse;

public interface BaseService<R,E> {
    E findById(Long id);

    PageResponse<E> getAll(int page, int size);

    void create(R req);

    void update(R req, Long id);

    void delete(Long id);
}
