package com.inventotymate.business.service.impl;

import com.inventotymate.business.Dto.OrderDetailRequest;
import com.inventotymate.business.Dto.OrderRequest;
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

import java.time.LocalDate;
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
    public Order createOrder(OrderRequest orderRequestDTO) {
        // Crear y guardar la orden
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());

        // Inicializar la lista de detalles
        List<OrderDetail> orderDetails = new ArrayList<>();
        double total = 0.0;

        for (OrderDetailRequest orderDetailRequest : orderRequestDTO.getOrderDetails()) {
            Product product = productRepository.findById(orderDetailRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder); // Enlazar con la orden
            orderDetail.setProduct(product);
            orderDetail.setQuantity(orderDetailRequest.getQuantity());
            orderDetail.setSubtotalPrice(product.getProductPrice() * orderDetailRequest.getQuantity());

            total += orderDetail.getSubtotalPrice();
            orderDetails.add(orderDetail);
        }

        newOrder.setTotalPrice(total);
        newOrder.setOrderDetails(orderDetails); // Enlazar los detalles con la orden

        // Guardar la orden y los detalles en cascada
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
