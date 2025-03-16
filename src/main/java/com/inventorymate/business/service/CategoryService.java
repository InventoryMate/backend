package com.inventorymate.business.service;

import com.inventorymate.business.Dto.CategoryRequest;
import com.inventorymate.business.model.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> getAllCategories();
    public Category getCategoryById(Long categoryId);
    public Category saveCategory(CategoryRequest category);
    public Category updateCategory(CategoryRequest category, Long categoryId);
    public void deleteCategory(Long categoryId);
}
