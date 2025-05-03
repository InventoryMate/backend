package com.inventorymate.ai.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.inventorymate.business.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_prediction_settings")
public class ProductPredictionSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonBackReference
    private Product product;

    @Column(name = "estimated_weekly_sales", nullable = false)
    private int estimatedWeeklySales;
}
