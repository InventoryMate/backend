package com.inventorymate.business.service.impl;

import com.inventorymate.business.model.Category;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.service.CategoryService;
import org.springframework.stereotype.Service;

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
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        Category categoryToUpdate = categoryRepository.findById(category.getId()).orElse(null);
        if (categoryToUpdate != null) {
            categoryToUpdate.setCategoryName(category.getCategoryName());
            return categoryRepository.save(categoryToUpdate);
        }
        else {
            return null;
        }
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (categoryRepository.existsById(categoryId)) {
            categoryRepository.deleteById(categoryId);
        }
    }
}
