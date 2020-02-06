package com.ordermeow.api.product;

import org.springframework.stereotype.Service;

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

