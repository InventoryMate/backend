package com.inventorymate.business.controller;

import com.inventorymate.business.dto.OrderRequest;
import com.inventorymate.business.dto.OrderResponse;
import com.inventorymate.business.dto.ProductSalesRequest;
import com.inventorymate.business.dto.ProductWeeklySalesResponse;
import com.inventorymate.business.model.Order;

import com.inventorymate.business.service.OrderService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<OrderResponse>> getAllOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<OrderResponse> orders = orderService.getAllOrders(userDetails.getStoreId());
        return orders.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(orders);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: GET
    // Description: Get order by id
    @Transactional(readOnly = true)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable(name = "orderId") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId, userDetails.getStoreId()));
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/orders
    // Method: POST
    // Description: Save order
    @Transactional
    @PostMapping
    public ResponseEntity<OrderResponse>createOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderRequest, userDetails.getStoreId()));
    }


    // URL: http://localhost:8081/api/InventoryMate/v1/orders/weekly-sales
    // Method: POST
    // Description: Get weekly sales for products
    @Transactional
    @PostMapping("/weekly-sales")
    public ResponseEntity<List<ProductWeeklySalesResponse>> getWeeklySales(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProductSalesRequest request) {

        List<ProductWeeklySalesResponse> sales = orderService.getWeeklySalesForProducts(request.getProductIds(), userDetails.getStoreId());
        return ResponseEntity.ok(sales);
    }

    // Normally we don't update orders
    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: PUT
    // Description: Update order
    @Transactional
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable(name = "orderId") Long orderId,
                                             @RequestBody OrderRequest updatedOrder) {
        return ResponseEntity.ok(orderService.updateOrder(updatedOrder, orderId, userDetails.getStoreId()));
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: DELETE
    // Description: Delete order
    @Transactional
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long orderId) {
        orderService.deleteOrder(orderId, userDetails.getStoreId());
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
