package com.inventorymate.business.service;

import com.inventorymate.business.model.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> getAllCategories();
    public Category getCategoryById(Long categoryId);
    public Category saveCategory(Category category);
    public Category updateCategory(Category category);
    public void deleteCategory(Long categoryId);
}
