package com.inventorymate.business.service;

import com.inventorymate.business.Dto.CategoryRequest;
import com.inventorymate.business.model.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> getAllCategories(Long storeId);
    public Category getCategoryById(Long categoryId, Long storeId);
    public Category saveCategory(CategoryRequest category, Long storeId);
    public Category updateCategory(CategoryRequest category, Long categoryId, Long storeId);
    public void deleteCategory(Long categoryId, Long storeId);
}
