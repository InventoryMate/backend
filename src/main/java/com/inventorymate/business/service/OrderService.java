package com.inventorymate.business.service;

import com.inventorymate.business.dto.OrderRequest;
import com.inventorymate.business.dto.OrderResponse;
import com.inventorymate.business.model.Order;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequests, Long storeId);
    public List<Order> getAllOrders(Long storeId);
    public Order getOrderById(Long orderId, Long storeId);
    public Order updateOrder(OrderRequest order, Long orderId, Long storeId);
    public void deleteOrder(Long orderId, Long storeId);
}
