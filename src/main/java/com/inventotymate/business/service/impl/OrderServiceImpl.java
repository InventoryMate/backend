package com.inventotymate.business.service.impl;

import com.inventotymate.business.Dto.OrderDetailRequest;
import com.inventotymate.business.model.Order;
import com.inventotymate.business.model.OrderDetail;
import com.inventotymate.business.model.Product;
import com.inventotymate.business.repository.OrderDetailRepository;
import com.inventotymate.business.repository.OrderRepository;
import com.inventotymate.business.repository.ProductRepository;
import com.inventotymate.business.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;


    @Transactional
    @Override
    public Order createOrder(List<OrderDetailRequest> orderDetailRequests) {
        double totalPrice = 0;
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());

        List<OrderDetail> orderDetails= new ArrayList<>();

        for (OrderDetailRequest request: orderDetailRequests) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            double subtotal = product.getProductPrice() * request.getQuantity();
            totalPrice += subtotal;

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(request.getQuantity());
            orderDetail.setSubtotalPrice(subtotal);
            orderDetails.add(orderDetail);
        }

        order.setTotalPrice(totalPrice);
        order = orderRepository.save(order);
        for (OrderDetail detail: orderDetails) {
            detail.setOrder(order);
            orderDetailRepository.save(detail);
        }

        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        Order orderToUpdate = orderRepository.findById(order.getId()).orElse(null);
        if (orderToUpdate != null) {
            orderToUpdate.setOrderDate(order.getOrderDate());
            orderToUpdate.setTotalPrice(order.getTotalPrice());
            return orderRepository.save(orderToUpdate);
        }
        else {
            return null;
        }
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
        }
    }
}
