package com.ordermeow.api.order;

import com.ordermeow.api.product.ProductEntity;
import com.ordermeow.api.product.ProductExceptions;
import com.ordermeow.api.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private ProductRepository productRepository;

    private OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public BigDecimal calculateTotal(List<Long> products) {
        BigDecimal cost = BigDecimal.ZERO;

        for (Long productId : products) {
            ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new ProductExceptions.ProductNotFound(productId));
            cost = cost.add(product.getProductPrice());
        }

        return cost.setScale(2, BigDecimal.ROUND_CEILING);
    }

    public List<OrderEntity> createOrder(List<Long> products, BigDecimal expectedTotal) {
        List<ProductEntity> orderedProducts = verifyTotal(products, expectedTotal);
        UUID orderUuid = UUID.randomUUID();
        List<OrderEntity> completeOrder = new ArrayList<>();

        for (ProductEntity product : orderedProducts) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderUuid(orderUuid.toString());
            orderEntity.setTotal(expectedTotal);
            orderEntity.setProductName(product.getProductName());
            orderEntity.setProductPrice(product.getProductPrice());
            completeOrder.add(orderRepository.save(orderEntity));
        }

        return completeOrder;
    }

    public List<OrderEntity> getOrder(String orderNumber) {
        return orderRepository.findByOrderUuid(orderNumber).orElseThrow(() -> new OrderExceptions.OrderNotFound(orderNumber));
    }


    /**
     * Confirm that the previous total matches the current total - ensures that the prices didn't
     * change between the customer checking their cart and actually placing the order
     */
    private List<ProductEntity> verifyTotal(List<Long> products, BigDecimal expectedTotal) {
        BigDecimal cost = BigDecimal.ZERO;

        List<ProductEntity> productEntityList = new ArrayList<>();

        for (Long productId : products) {
            ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new ProductExceptions.ProductNotFound(productId));
            productEntityList.add(product);
            cost = cost.add(product.getProductPrice());
        }

        // Force consistent scaling on the two prices
        cost = cost.setScale(2, BigDecimal.ROUND_CEILING);
        expectedTotal = expectedTotal.setScale(2, BigDecimal.ROUND_CEILING);

        if (!cost.equals(expectedTotal)) {
            throw new OrderExceptions.DifferentTotalPrices(expectedTotal, cost);
        }

        return productEntityList;
    }
}

