package com.ordermeow.api.product;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductEntity createProduct(ProductEntity product, MultipartFile file) throws RuntimeException {
        if (product.getProductName() == null || product.getProductName().isEmpty()) {
            throw new ProductExceptions.BadProductName(product.getProductName());
        }
        if (product.getProductDescription() == null || product.getProductDescription().isEmpty()) {
            throw new ProductExceptions.BadProductDescription(product.getProductDescription());
        }
        if (product.getProductPrice() == null || product.getProductPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductExceptions.BadProductPrice(product.getProductPrice());
        }

        if (file != null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            if (fileName.contains("..")) {
                throw new ProductExceptions.InvalidFileException();
            }

            try {
                product.setFileName(fileName);
                product.setFileType(file.getContentType());
                product.setProductImage(file.getBytes());
            } catch (IOException ex) {
                throw new ProductExceptions.InvalidFileException();
            }
        }

        return productRepository.save(product);
    }

    public ProductEntity editProduct(ProductEntity product, MultipartFile file) {
        if (product.getProductId() == null) {
            throw new ProductExceptions.ProductNotFound(-1L);
        }

        ProductEntity productToUpdate = productRepository.findById(product.getProductId()).orElseThrow(() -> new ProductExceptions.ProductNotFound(product.getProductId()));

        // Update product fields if they are present
        if (product.getProductName() != null && !product.getProductName().isEmpty()) {
            productToUpdate.setProductName(product.getProductName());
        }

        if (product.getProductDescription() != null && !product.getProductDescription().isEmpty()) {
            productToUpdate.setProductDescription(product.getProductDescription());
        }

        if (product.getProductPrice() != null && product.getProductPrice().compareTo(BigDecimal.ZERO) > 0) {
            productToUpdate.setProductPrice(product.getProductPrice());
        }

        if (file != null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            if (!fileName.contains("..")) {
                try {
                    productToUpdate.setFileName(fileName);
                    productToUpdate.setFileType(file.getContentType());
                    productToUpdate.setProductImage(file.getBytes());
                } catch (IOException ignored) {
                }
            }
        }

        return productRepository.save(productToUpdate);
    }

    public ProductEntity getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductExceptions.ProductNotFound(id));
    }

    public List<ProductEntity> getProducts() {
        List<ProductEntity> products = new ArrayList<>();
        productRepository.findAll().iterator().forEachRemaining(products::add);
        return products;
    }


    public void deleteProductById(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductExceptions.ProductNotFound(productId));
        productRepository.deleteById(productId);
    }
}

