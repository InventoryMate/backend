package com.inventotymate.business.service;

import com.inventotymate.business.Dto.OrderDetailRequest;
import com.inventotymate.business.model.Order;
import com.inventotymate.business.model.Product;

import java.util.List;

public interface OrderService {
    Order createOrder(List<OrderDetailRequest> orderDetailRequests);
    public List<Order> getAllOrders();
    public Order getOrderById(Long orderId);
    public Order saveOrder(Order order);
    public Order updateOrder(Order order);
    public void deleteOrder(Long orderId);
}
