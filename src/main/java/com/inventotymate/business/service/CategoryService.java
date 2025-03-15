package com.inventotymate.business.service;

import com.inventotymate.business.Dto.OrderRequest;
import com.inventotymate.business.model.Category;
import com.inventotymate.business.model.Order;

import java.util.List;

public interface CategoryService {
    public List<Category> getAllCategories();
    public Category getCategoryById(Long categoryId);
    public Category saveCategory(Category category);
    public Category updateCategory(Category category);
    public void deleteCategory(Long categoryId);
}
