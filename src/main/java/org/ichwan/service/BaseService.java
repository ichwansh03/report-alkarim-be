package org.ichwan.service;

import org.ichwan.dto.PageResponse;

import java.util.List;

public interface BaseService<E> {
    E findById(Long id);

    PageResponse<E> getAll(int page, int size);
}
