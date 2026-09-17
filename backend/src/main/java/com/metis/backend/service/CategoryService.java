package com.metis.backend.service;

import com.metis.backend.dto.CategoryRequest;
import com.metis.backend.dto.CategoryResponse;
import com.metis.backend.entity.Category;
import com.metis.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import com.metis.backend.exception.ConflictException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<CategoryResponse> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::toResponse);
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Category already exists");
        }

        Category category = new Category(request.getName());

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }

        categoryRepository.deleteById(id);
        return true;
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}