package com.ordermeow.api.product;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

@Service
public class ProductService {
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductEntity createProduct(ProductEntity product, MultipartFile file) throws ProductExceptions.BadProductName {
        if (product.getProductName() == null || product.getProductName().isEmpty()) {
            throw new ProductExceptions.BadProductName(product.getProductName());
        }
        if (product.getProductDescription() == null || product.getProductDescription().isEmpty()) {
            throw new ProductExceptions.DescriptionNotFound(product.getProductDescription());
        }
        if (product.getProductPrice() == null || product.getProductPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductExceptions.PriceNotFound(product.getProductPrice());
        }

        if (file != null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            if (fileName.contains("..")) {
                throw new ProductExceptions.FileNameInvalid(fileName);
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

    public ProductEntity getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductExceptions.ProductNotFound(id));

    }

    public void deleteProductById(Long productId) {
        productRepository.findById(productId).orElseThrow(() -> new ProductExceptions.ProductNotFound(productId));
        productRepository.deleteById(productId);
    }
}

