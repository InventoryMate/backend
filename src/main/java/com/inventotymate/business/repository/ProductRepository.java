package com.inventotymate.business.repository;

import com.inventotymate.business.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product, Long> {
    // Maybe a relation to a Category entity (Many to One) or maybe we won't use Categories in the future
    List<Product> findByCategoryId(Long CategoryId);
}
