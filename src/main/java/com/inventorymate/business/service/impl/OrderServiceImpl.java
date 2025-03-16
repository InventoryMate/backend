package com.inventorymate.business.service.impl;

import com.inventorymate.business.Dto.OrderDetailRequest;
import com.inventorymate.business.Dto.OrderRequest;
import com.inventorymate.business.model.Order;
import com.inventorymate.business.model.OrderDetail;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.repository.OrderDetailRepository;
import com.inventorymate.business.repository.OrderRepository;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.service.OrderService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private OrderDetailRepository orderDetailRepository;
    private ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
    }


    @Transactional
    @Override
    public Order createOrder(OrderRequest orderRequestDTO) {
        // Validate order request
        validateOrderRequest(orderRequestDTO);

        // Create and save the order
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());

        // Initialize the list of order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        double total = 0.0;

        for (OrderDetailRequest orderDetailRequest : orderRequestDTO.getOrderDetails()) {
            Product product = productRepository.findById(orderDetailRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder); // Link the order with the detail
            orderDetail.setProduct(product);
            orderDetail.setQuantity(orderDetailRequest.getQuantity());
            orderDetail.setSubtotalPrice(product.getProductPrice() * orderDetailRequest.getQuantity());

            total += orderDetail.getSubtotalPrice();
            orderDetails.add(orderDetail);
        }

        newOrder.setTotalPrice(total);
        newOrder.setOrderDetails(orderDetails); // Link the order with the details

        // Save the order and the details
        return orderRepository.save(newOrder);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    // Normally this method is not used.
    @Transactional
    @Override
    public Order updateOrder(OrderRequest order, Long orderId) {
        return null;
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order with Id " + orderId + " not found");
        }
        orderRepository.deleteById(orderId);
    }

    private void validateOrderRequest(OrderRequest orderRequest) {
        if (orderRequest.getOrderDetails() == null || orderRequest.getOrderDetails().isEmpty()) {
            throw new ValidationException("Order must have at least one product.");
        }

        for (OrderDetailRequest detail : orderRequest.getOrderDetails()) {
            if (detail.getProductId() == null) {
                throw new ValidationException("Each order detail must have a valid product ID.");
            }

            if (detail.getQuantity() <= 0) {
                throw new ValidationException("Product quantity must be greater than zero.");
            }
        }
    }
}
