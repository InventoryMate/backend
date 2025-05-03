package com.inventorymate.business.service.impl;

import com.inventorymate.business.dto.*;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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
    public OrderResponse createOrder(OrderRequest orderRequestDTO, Long storeId) {
        // Validate the order request
        validateOrderRequest(orderRequestDTO);

        // Create a new order
        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        newOrder.setStore(store);

        List<OrderDetail> orderDetails = new ArrayList<>();
        List<String> insufficientStockProducts = new ArrayList<>();
        double total = 0.0;

        for (OrderDetailRequest orderDetailRequest : orderRequestDTO.getOrderDetails()) {
            Product product = productRepository.findById(orderDetailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Get available stock sorted by purchase date (FIFO strategy)
            List<Stock> stocks = stockRepository.findByProductIdAndStore_IdOrderByPurchaseDateAsc(product.getId(), storeId)
                    .stream()
                    .filter(stock -> !stock.isExpired()) // Ignore expired stock
                    .toList();

            long remainingQuantity = orderDetailRequest.getQuantity();
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

        Order savedOrder = orderRepository.save(newOrder);
        return mapToOrderResponse(savedOrder); // ✅
    }


    @Override
    public List<OrderResponse> getAllOrders(Long storeId) {
        return orderRepository.findByStore_Id(storeId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId, Long storeId) {
        Order order = orderRepository.findByIdAndStore_Id(orderId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " not found in this store"));
        return mapToOrderResponse(order);
    }


    @Override
    public List<ProductWeeklySalesResponse> getWeeklySalesForProducts(List<Long> productIds, Long storeId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(6); // últimos 7 días incluyendo hoy

        List<Order> orders = orderRepository.findByStore_IdAndOrderDateBetween(storeId, startDate, endDate);

        Map<Long, ProductWeeklySalesResponse> resultMap = new HashMap<>();

        for (Order order : orders) {
            DayOfWeek day = order.getOrderDate().getDayOfWeek();
            String dayName = day.getDisplayName(TextStyle.FULL, new Locale("es", "ES")); // "Lunes", "Martes", etc.

            for (OrderDetail detail : order.getOrderDetails()) {
                Long productId = detail.getProduct().getId();
                if (productIds.contains(productId)) {
                    ProductWeeklySalesResponse response = resultMap.computeIfAbsent(productId, id -> {
                        ProductWeeklySalesResponse r = new ProductWeeklySalesResponse();
                        r.setProductId(id);
                        r.setProductName(detail.getProduct().getProductName());
                        r.setDailySales(new HashMap<>());
                        return r;
                    });

                    Map<String, Integer> dailySales = response.getDailySales();
                    dailySales.put(dayName, dailySales.getOrDefault(dayName, 0) + detail.getQuantity());
                }
            }
        }

        return new ArrayList<>(resultMap.values());
    }

    // Normally this method is not used.
    @Transactional
    @Override
    public OrderResponse updateOrder(OrderRequest order, Long orderId, Long storeId) {
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

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalPrice(order.getTotalPrice());

        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream().map(detail -> {
            OrderDetailResponse detailResponse = new OrderDetailResponse();
            detailResponse.setProductId(detail.getProduct().getId());
            detailResponse.setProductName(detail.getProduct().getProductName());
            detailResponse.setQuantity(detail.getQuantity());
            detailResponse.setSubtotalPrice(detail.getSubtotalPrice());
            return detailResponse;
        }).collect(Collectors.toList());

        response.setOrderDetails(detailResponses);
        return response;
    }
}
