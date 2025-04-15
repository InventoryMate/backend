package com.inventorymate.business.repository;

import com.inventorymate.business.model.Category;
import com.inventorymate.business.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //List<Order> findOrderBy(LocalDate orderDate);
    List<Order> findByStore_Id(Long storeId);
    Optional<Order> findByIdAndStore_Id(Long orderId, Long storeId);
    boolean existsByIdAndStore_Id(Long categoryId, Long storeId);
    @EntityGraph(attributePaths = {"orderDetails", "orderDetails.product"})
    Optional<Order> findById(Long id);
}
