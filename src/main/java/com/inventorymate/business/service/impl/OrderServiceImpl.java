package com.inventorymate.business.service.impl;

import com.inventorymate.business.dto.OrderDetailRequest;
import com.inventorymate.business.dto.OrderRequest;
import com.inventorymate.business.model.Order;
import com.inventorymate.business.model.OrderDetail;
import com.inventorymate.business.model.Product;
import com.inventorymate.business.model.Stock;
import com.inventorymate.business.repository.OrderRepository;
import com.inventorymate.business.repository.ProductRepository;
import com.inventorymate.business.repository.StockRepository;
import com.inventorymate.business.service.OrderService;
import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private StockRepository stockRepository;
    private ProductRepository productRepository;
    private StoreRepository storeRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            StockRepository stockRepository,
                            ProductRepository productRepository,
                            StoreRepository storeRepository) {
        this.orderRepository = orderRepository;
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }


    @Transactional
    @Override
    public Order createOrder(OrderRequest orderRequestDTO, Long storeId) {
        // Validate the order request
        validateOrderRequest(orderRequestDTO);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        // Create a new order
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStore(store);

        List<OrderDetail> orderDetails = new ArrayList<>();
        List<String> insufficientStockProducts = new ArrayList<>();
        double total = 0.0;

        for (OrderDetailRequest orderDetailRequest : orderRequestDTO.getOrderDetails()) {
            Product product = productRepository.findByIdAndStore_Id(orderDetailRequest.getProductId(), storeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Get available stock sorted by purchase date (FIFO strategy)
            List<Stock> stocks = stockRepository.findByProductIdAndStore_IdOrderByPurchaseDateAsc(product.getId(), storeId)
                    .stream()
                    .filter(stock -> !stock.isExpired()) // Ignore expired stock
                    .toList();

            int remainingQuantity = orderDetailRequest.getQuantity();
            Iterator<Stock> stockIterator = stocks.iterator();

            while (remainingQuantity > 0 && stockIterator.hasNext()) {
                Stock stock = stockIterator.next();

                if (stock.getQuantity() >= remainingQuantity) {
                    stock.consumeStock(remainingQuantity);
                    remainingQuantity = 0;
                } else {
                    remainingQuantity -= stock.getQuantity();
                    stock.consumeStock(stock.getQuantity());
                }

                // If stock reaches 0, delete it
                if (stock.getQuantity() == 0) {
                    stockRepository.delete(stock);
                } else {
                    stockRepository.save(stock); // Save updated stock
                }
            }

            // If there is not enough stock, add the product to the insufficient stock list
            if (remainingQuantity > 0) {
                insufficientStockProducts.add(product.getProductName());
            } else {
                // Add order detail only if enough stock is available
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(newOrder);
                orderDetail.setProduct(product);
                orderDetail.setQuantity(orderDetailRequest.getQuantity());
                orderDetail.setSubtotalPrice(product.getProductPrice() * orderDetailRequest.getQuantity());

                total += orderDetail.getSubtotalPrice();
                orderDetails.add(orderDetail);
            }
        }

        // If any product has insufficient stock, throw an exception listing all affected products
        if (!insufficientStockProducts.isEmpty()) {
            throw new ValidationException("Not enough stock for the following products: " + String.join(", ", insufficientStockProducts));
        }

        // Save the order
        newOrder.setTotalPrice(total);
        newOrder.setOrderDetails(orderDetails);

        return orderRepository.save(newOrder);
    }


    @Override
    public List<Order> getAllOrders(Long storeId) {
        return orderRepository.findByStore_Id(storeId);
    }

    @Override
    public Order getOrderById(Long orderId, Long storeId) {
        return orderRepository.findByIdAndStore_Id(orderId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found in this store"));
    }

    // Normally this method is not used.
    @Transactional
    @Override
    public Order updateOrder(OrderRequest order, Long orderId, Long storeId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Long storeId) {
        Order order = orderRepository.findByIdAndStore_Id(orderId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        orderRepository.delete(order);
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
