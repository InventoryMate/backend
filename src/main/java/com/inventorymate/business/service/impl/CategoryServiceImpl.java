package com.inventorymate.business.service.impl;

import com.inventorymate.business.Dto.CategoryRequest;
import com.inventorymate.business.model.Category;
import com.inventorymate.business.model.Order;
import com.inventorymate.business.repository.CategoryRepository;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.service.CategoryService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               StoreRepository storeRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    public List<Category> getAllCategories(Long storeId) {
        return categoryRepository.findByStore_Id(storeId);
    }

    @Override
    public Category getCategoryById(Long categoryId, Long storeId) {
        return categoryRepository.findByIdAndStore_Id(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found in this store"));
    }

    @Override
    @Transactional
    public Category saveCategory(CategoryRequest newCategory, Long storeId) {
        return createOrUpdateCategory(newCategory, new Category(), null, storeId);
    }

    @Override
    @Transactional
    public Category updateCategory(CategoryRequest categoryRequest, Long categoryId, Long storeId) {
        Category categoryToUpdate = getCategoryById(categoryId, storeId);
        
        return createOrUpdateCategory(categoryRequest, categoryToUpdate, categoryId, storeId);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId, Long storeId) {

        Category category = categoryRepository.findByIdAndStore_Id(categoryId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Actualizar los productos que tienen esta categoría, estableciendo categoryId como null
        productRepository.updateCategoryToNull(categoryId);

        // Ahora sí, eliminar la categoría
        categoryRepository.delete(category);
    }

    private Category createOrUpdateCategory(CategoryRequest categoryRequest, Category category, Long categoryId, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        category.setStore(store);
        category.setCategoryName(categoryRequest.getCategoryName());

        validateCategoryFormat(categoryRequest);
        validateUniqueCategoryName(categoryRequest.getCategoryName(), storeId, categoryId);

        return categoryRepository.save(category);
    }

    private void validateCategoryFormat(CategoryRequest categoryRequest) {
        if (categoryRequest.getCategoryName() == null || categoryRequest.getCategoryName().trim().isEmpty()) {
            throw new ValidationException("Category name is required.");
        }
        if (categoryRequest.getCategoryName().length() < 3) {
            throw new ValidationException("Category name must be at least 3 characters long.");
        }
        if (categoryRequest.getCategoryName().length() > 50) {
            throw new ValidationException("Category name must not exceed 50 characters.");
        }
        if (!categoryRequest.getCategoryName().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ ]+$")) {
            throw new ValidationException("Category name can only contain letters, numbers, and spaces.");
        }
    }

    private void validateUniqueCategoryName(String categoryName, Long storeId, Long categoryId) {
        Category existingCategory = categoryRepository.findByCategoryNameIgnoreCaseAndStore_Id(categoryName, storeId);
        if (existingCategory != null && (categoryId == null || !existingCategory.getId().equals(categoryId))) {
            throw new ValidationException("Category name already exists in this store.");
        }
    }

}
