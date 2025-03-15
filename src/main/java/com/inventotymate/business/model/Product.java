package com.inventotymate.business.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "has_expiration", nullable = false)
    private boolean hasExpiration;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date")
    private Date expirationDate;

    public boolean isExpired() {
        return hasExpiration && expirationDate != null && expirationDate.before(new Date());
    }
}
