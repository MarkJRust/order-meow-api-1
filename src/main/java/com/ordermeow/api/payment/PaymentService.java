package com.ordermeow.api.payment;

import com.ordermeow.api.product.ProductEntity;
import com.ordermeow.api.product.ProductExceptions;
import com.ordermeow.api.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {
    private ProductRepository productRepository;

    public PaymentService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public BigDecimal calculateTotal(List<Long> products) {
        BigDecimal cost = BigDecimal.ZERO;

        for (Long productId : products) {
            ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new ProductExceptions.ProductNotFound(productId));
            cost = cost.add(product.getProductPrice());
        }

        return cost.setScale(2, BigDecimal.ROUND_CEILING);
    }
}

