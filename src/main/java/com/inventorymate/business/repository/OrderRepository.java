package com.inventorymate.business.repository;

import com.inventorymate.business.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //List<Order> findOrderBy(LocalDate orderDate);

    @EntityGraph(attributePaths = {"orderDetails", "orderDetails.product"})
    Optional<Order> findById(Long id);
}
