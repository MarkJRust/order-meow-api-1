package com.ordermeow.api.product;

import org.springframework.stereotype.Service;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductEntity createProduct(ProductEntity product) throws ProductExceptions.BadProductName {
        if (product.getProductName() == null || product.getProductName().isEmpty()) {
            throw new ProductExceptions.BadProductName(product.getProductName());
        }
        if (product.getProductDescription() == null || product.getProductDescription().isEmpty()) {
            throw new ProductExceptions.DescriptionNotFound(product.getProductDescription());
        }
        if (product.getProductPrice() == null || product.getProductPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductExceptions.PriceNotFound(product.getProductPrice());
        }

        return productRepository.save(product);
    }

    public ProductEntity getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductExceptions.ProductNotFound(id));

    }

    public void deleteProductById(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductExceptions.ProductNotFound(productId));
        productRepository.deleteById(productId);
    }
}

