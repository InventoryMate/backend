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

    // Normalmente no se utiliza este método, y falta arreglarse.
    @Transactional
    @Override
    public Order updateOrder(Order order) {
        Order orderToUpdate = orderRepository.findById(order.getId()).orElseThrow(() ->
                new RuntimeException("Orden no encontrada"));
        orderToUpdate.setOrderDate(order.getOrderDate());
        double newTotalPrice = 0.0;
        List<OrderDetail> existingDetails = orderToUpdate.getOrderDetails();
        Map<Long, OrderDetail> existingDetailsMap = existingDetails.stream()
                .collect(Collectors.toMap(detail -> detail.getProduct().getId(), detail -> detail));
        List<OrderDetail> updatedDetails = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Product product = productRepository.findById(orderDetail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            OrderDetail detailToUpdate = existingDetailsMap.get(product.getId());
            if (detailToUpdate != null) {
                detailToUpdate.setQuantity(orderDetail.getQuantity());
                detailToUpdate.setSubtotalPrice(product.getProductPrice() * orderDetail.getQuantity());
            } else {
                detailToUpdate = new OrderDetail();
                detailToUpdate.setOrder(orderToUpdate);
                detailToUpdate.setProduct(product);
                detailToUpdate.setQuantity(orderDetail.getQuantity());
                detailToUpdate.setSubtotalPrice(product.getProductPrice() * orderDetail.getQuantity());
            }
            newTotalPrice += detailToUpdate.getSubtotalPrice();
            updatedDetails.add(detailToUpdate);
        }
        existingDetails.removeIf(detail ->
                updatedDetails.stream().noneMatch(updated -> updated.getProduct().getId().equals(detail.getProduct().getId())));
        orderToUpdate.setOrderDetails(updatedDetails);
        orderToUpdate.setTotalPrice(newTotalPrice);
        return orderRepository.save(orderToUpdate);
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
        }
    }
}
