package com.ordermeow.api.product;

import java.math.BigDecimal;

public class ProductExceptions {

    public static class BadProductName extends RuntimeException {
        BadProductName(String productName) {
            super("The name passed in was not valid: " + productName);
        }
    }

    public static class ProductNotFound extends RuntimeException {
        public ProductNotFound(Long productId) {
            super("The product was not found: " + productId);
        }
    }

    public static class BadProductDescription extends RuntimeException {
        BadProductDescription(String productDescription) {
            super("The product description was not specified " + productDescription);
        }
    }

    public static class BadProductPrice extends RuntimeException {
        public BadProductPrice(BigDecimal price) {
            super("The product price is not valid: " + price);
        }
    }

    public static class InvalidFileException extends RuntimeException {
        InvalidFileException() {
            super("The file uploaded is not valid");
        }
    }

}
