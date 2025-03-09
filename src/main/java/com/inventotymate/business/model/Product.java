package com.inventotymate.business.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 50)
    private String productName;

    @Column(name = "product_description", length = 100)
    private String productDescription;

    @Column(name = "product_price", nullable = false)
    private double productPrice;

    // Maybe a relation to a Category entity (Many to One) or maybe we won't use Categories in the future
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
}
