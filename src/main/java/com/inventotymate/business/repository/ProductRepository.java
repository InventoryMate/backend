package com.inventotymate.business.repository;

import com.inventotymate.business.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long CategoryId);
}
