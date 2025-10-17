package org.ichwan.service.impl;

import org.ichwan.domain.Category;
import org.ichwan.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {
    @Mock
    CategoryRepository categoryRepository;
    @InjectMocks
    CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.listAll()).thenReturn(Collections.emptyList());
        List<Category> categories = categoryService.getAllCategories();
        assertNotNull(categories);
    }

    @Test
    void testCreateCategory() {
        categoryService.createCategory("Math");
        verify(categoryRepository, times(1)).persistAndFlush(any(Category.class));
    }

    @Test
    void testDeleteCategory() {
        Category category = new Category("Math");
        when(categoryRepository.findById(1L)).thenReturn(category);
        categoryService.deleteCategory(1L);
        verify(categoryRepository, times(1)).delete(category);
    }
}

