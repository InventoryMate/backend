package com.inventotymate.business.service;

import com.inventotymate.business.Dto.OrderRequest;
import com.inventotymate.business.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderRequest orderRequests);
    public List<Order> getAllOrders();
    public Order getOrderById(Long orderId);
    public Order saveOrder(Order order);
    public Order updateOrder(Order order);
    public void deleteOrder(Long orderId);
}
