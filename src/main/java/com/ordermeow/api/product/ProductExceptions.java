package com.ordermeow.api.product;

public class ProductExceptions {

    public static class BadProductName extends RuntimeException {
        BadProductName(String productName) {
            super("The name passed in was not valid: " + productName);
        }
    }

    public static class ProductNotFound extends RuntimeException {
        ProductNotFound(Long productId) {
            super("The product was not found: " + productId);
        }
    }
}
