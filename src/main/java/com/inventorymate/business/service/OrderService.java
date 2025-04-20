package com.inventorymate.business.service;

import com.inventorymate.business.Dto.OrderRequest;
import com.inventorymate.business.Dto.OrderResponse;
import com.inventorymate.business.model.Order;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequests);
    public List<Order> getAllOrders();
    public Order getOrderById(Long orderId);
    public Order updateOrder(OrderRequest order, Long orderId);
    public void deleteOrder(Long orderId);
}
