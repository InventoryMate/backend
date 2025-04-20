package com.inventorymate.business.controller;

import com.inventorymate.business.Dto.OrderRequest;
import com.inventorymate.business.Dto.OrderResponse;
import com.inventorymate.business.model.Order;

import com.inventorymate.business.service.OrderService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

//CHANGE REQUEST MAPPING TO /api/InventoryMate/v1/orders
@Controller
@RequestMapping("/api/InventoryMate/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/orders
    // Method: GET
    // Description: Get all orders
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return orders.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(orders);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: GET
    // Description: Get order by id
    @Transactional(readOnly = true)
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable(name = "orderId") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/orders
    // Method: POST
    // Description: Save order
    @Transactional
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequest));
    }

    // Normally we don't update orders
    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: PUT
    // Description: Update order
    @Transactional
    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable(name = "orderId") Long orderId, @RequestBody OrderRequest updatedOrder) {
        Order order = orderService.updateOrder(updatedOrder, orderId);
        return ResponseEntity.ok(order);

    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: DELETE
    // Description: Delete order
    @Transactional
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // Global Exception Handling for Not Found & Validation Exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
