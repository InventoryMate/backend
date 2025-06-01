package com.inventorymate.business.service;

import com.inventorymate.business.dto.OrderRequest;
import com.inventorymate.business.dto.OrderResponse;
import com.inventorymate.business.dto.ProductWeeklySalesResponse;
import com.inventorymate.business.model.Order;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequests, Long storeId);
    public List<OrderResponse> getAllOrders(Long storeId);
    public OrderResponse getOrderById(Long orderId, Long storeId);
    public OrderResponse updateOrder(OrderRequest order, Long orderId, Long storeId);
    public void deleteOrder(Long orderId, Long storeId);
    public List<ProductWeeklySalesResponse> getWeeklySalesForProducts(Long storeId);
}
