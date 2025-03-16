package com.inventorymate.business.service.impl;

import com.inventorymate.business.Dto.CategoryRequest;
import com.inventorymate.business.model.Category;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.service.CategoryService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    @Override
    public Category saveCategory(CategoryRequest newCategory) {
        return createOrUpdateCategory(newCategory, new Category(), null);
    }

    @Override
    public Category updateCategory(CategoryRequest categoryRequest, Long categoryId) {
        Category categoryToUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with Id " + categoryId + " not found"));

        categoryToUpdate.setCategoryName(categoryRequest.getCategoryName());
        return createOrUpdateCategory(categoryRequest, categoryToUpdate, categoryId);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category with Id " + categoryId + " not found");
        }
        categoryRepository.deleteById(categoryId);
    }

    private Category createOrUpdateCategory(CategoryRequest categoryRequest, Category category, Long categoryId) {
        category.setCategoryName(categoryRequest.getCategoryName());

        validateCategory(category, categoryId);

        return categoryRepository.save(category);
    }

    private void validateCategory(Category category, Long categoryId) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new ValidationException("Category name is required.");
        }

        if (category.getCategoryName().length() < 3) {
            throw new ValidationException("Category name must be at least 3 characters long.");
        }

        if (category.getCategoryName().length() > 50) {
            throw new ValidationException("Category name must not exceed 50 characters.");
        }

        if (!category.getCategoryName().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ ]+$")) {
            throw new ValidationException("Category name can only contain letters, numbers, and spaces.");
        }

        // Check if category name already exists (case-insensitive)
        Category existingCategory = categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName());
        if (existingCategory != null && (categoryId == null || !existingCategory.getId().equals(categoryId))) {
            throw new ValidationException("Category name already exists.");
        }
    }

}
