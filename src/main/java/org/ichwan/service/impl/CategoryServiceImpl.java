package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Category;
import org.ichwan.repository.CategoryRepository;

import java.util.List;

@ApplicationScoped
public class CategoryServiceImpl {

    @Inject
    CategoryRepository repository;

    @CacheResult(cacheName = "allCategories")
    public List<Category> getAllCategories() {
        return repository.listAll();
    }

    @Transactional
    public void createCategory(String name) {
        repository.persistAndFlush(new Category(name));
    }

    @CacheInvalidate(cacheName = "allCategories")
    @Transactional
    public void deleteCategory(Long id) {
        Category category = repository.findById(id);
        if (category != null) {
            repository.delete(category);
        }
    }
}
