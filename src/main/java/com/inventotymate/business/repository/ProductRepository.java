package com.inventotymate.business.repository;

import com.inventotymate.business.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Long> {
    // Maybe a relation to a Category entity (Many to One) or maybe we won't use Categories in the future
    List<Product> findByCategoryId(Long CategoryId);
}
