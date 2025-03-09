package com.inventotymate.business.controller;

import com.inventotymate.business.Dto.OrderRequest;
import com.inventotymate.business.model.Order;
import com.inventotymate.business.repository.OrderDetailRepository;
import com.inventotymate.business.repository.OrderRepository;
import com.inventotymate.business.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/InventoryMate/v1")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderController(OrderService orderService,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/orders
    // Method: GET
    // Description: Get all orders
    @Transactional(readOnly = true)
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if(orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: GET
    // Description: Get order by id
    @Transactional(readOnly = true)
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable(name = "orderId") Long orderId) {
        if(orderRepository.existsById(orderId)) {
            return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    // URL: http://localhost:8081/api/InventoryMate/v1/orders
    // Method: POST
    // Description: Save order
    @Transactional
    @PostMapping("/orders")
    public ResponseEntity<Order>createOrder(@RequestBody OrderRequest orderRequest) {
        Order sabedOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(sabedOrder, HttpStatus.CREATED);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: PUT
    // Description: Update order
    @Transactional
    @PutMapping("/order/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order updatedOrder) {
        if(orderRepository.existsById(orderId)) {
            return new ResponseEntity<>(orderService.updateOrder(updatedOrder), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/order/{orderId}
    // Method: DELETE
    // Description: Delete order
    @Transactional
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        if(orderRepository.existsById(orderId)) {
            orderService.deleteOrder(orderId);
            return new ResponseEntity<>("order deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
