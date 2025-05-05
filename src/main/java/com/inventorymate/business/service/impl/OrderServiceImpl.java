package com.inventorymate.business.service.impl;

import com.inventorymate.business.dto.*;
import com.inventorymate.business.model.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        validateOrderRequest(orderRequestDTO);

        Order newOrder = new Order();
        newOrder.setOrderDate(LocalDateTime.now());

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        newOrder.setStore(store);

        List<OrderDetail> orderDetails = new ArrayList<>();
        List<String> insufficientStockProducts = new ArrayList<>();
        double total = 0.0;

        for (OrderDetailRequest request : orderRequestDTO.getOrderDetails()) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // Convertir la cantidad solicitada a la unidad del producto
            double quantityInProductUnit = convertToProductUnit(product, request.getUnitType(), request.getQuantity());

            // Obtener el stock disponible ordenado por fecha de compra
            List<Stock> stocks = stockRepository.findByProductIdAndStore_IdOrderByPurchaseDateAsc(product.getId(), storeId)
                    .stream()
                    .filter(stock -> !stock.isExpired())
                    .toList();

            double remainingQuantity = quantityInProductUnit;
            Iterator<Stock> stockIterator = stocks.iterator();

            while (remainingQuantity > 0 && stockIterator.hasNext()) {
                Stock stock = stockIterator.next();
                double stockQty = stock.getQuantity();

                if (stockQty >= remainingQuantity) {
                    stock.consumeStock(remainingQuantity);
                    remainingQuantity = 0;
                } else {
                    remainingQuantity -= stockQty;
                    stock.consumeStock(stockQty);
                }

                if (stock.getQuantity() == 0) {
                    stockRepository.delete(stock);
                } else {
                    stockRepository.save(stock);
                }
            }

            if (remainingQuantity > 0) {
                insufficientStockProducts.add(product.getProductName());
            } else {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(newOrder);
                detail.setProduct(product);
                detail.setQuantity(quantityInProductUnit); // cantidad en unidad base del producto
                detail.setSubtotalPrice(product.getProductPrice() * quantityInProductUnit);

                total += detail.getSubtotalPrice();
                orderDetails.add(detail);
            }
        }

        if (!insufficientStockProducts.isEmpty()) {
            throw new ValidationException("Not enough stock for: " + String.join(", ", insufficientStockProducts));
        }

        newOrder.setTotalPrice(total);
        newOrder.setOrderDetails(orderDetails);

        Order savedOrder = orderRepository.save(newOrder);
        return mapToOrderResponse(savedOrder);
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
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        // Generate las 7 days in a List (ex: 2025-04-27, ..., 2025-05-03)
        List<LocalDate> last7Days = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startDate.plusDays(i))
                .toList();

        // Get all Orders between the dates
        List<Order> orders = orderRepository.findByStore_IdAndOrderDateBetween(
                storeId,
                startDate.atStartOfDay(),
                today.atTime(LocalTime.MAX)
        );

        Map<Long, ProductWeeklySalesResponse> resultMap = new HashMap<>();

        // Initialize the map with all days with 0.0
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            Map<String, Double> dailySales = new LinkedHashMap<>();
            for (LocalDate date : last7Days) {
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                dailySales.put(dayName, 0.0);
            }

            ProductWeeklySalesResponse response = new ProductWeeklySalesResponse();
            response.setProductId(productId);
            response.setProductName(product.getProductName());
            response.setDailySales(dailySales);

            resultMap.put(productId, response);
        }

        for (Order order : orders) {
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            String dayName = orderDate.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));

            for (OrderDetail detail : order.getOrderDetails()) {
                Long productId = detail.getProduct().getId();
                if (resultMap.containsKey(productId)) {
                    Map<String, Double> dailySales = resultMap.get(productId).getDailySales();
                    dailySales.put(dayName, dailySales.get(dayName) + detail.getQuantity());
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

    private double convertToProductUnit(Product product, UnitType requestUnit, double requestQuantity) {
        UnitType productUnit = product.getUnitType();

        if (!productUnit.isCompatible(requestUnit)) {
            throw new ValidationException("Incompatible units: " + requestUnit + " with " + productUnit);
        }

        // Convertir a "base" (gramos o ml), luego convertir al producto
        double baseQuantity = requestUnit.toBase(requestQuantity);
        return productUnit.fromBase(baseQuantity);
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
