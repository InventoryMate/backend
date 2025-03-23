package com.inventorymate.user.model;

import com.inventorymate.business.model.Category;
import com.inventorymate.business.model.Order;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.model.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name", nullable = false, length = 50)
    private String storeName;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    /*
    private List<Product> products;
    private List<Order> orders;
    private List<Stock> stocks;
    private List<Category> categories;
     */
}
