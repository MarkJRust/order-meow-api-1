package com.ordermeow.api.product;

import java.math.BigDecimal;

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

    public static class DescriptionNotFound extends RuntimeException {
        DescriptionNotFound(String productDescription) {
            super("The product description was not specified " + productDescription);
        }
    }

    public static class PriceNotFound extends RuntimeException {
        PriceNotFound(BigDecimal productPrice) {
            super("The product price was specified incorrectly " + productPrice);
        }
    }

    public static class InvalidFileException extends RuntimeException {
        InvalidFileException() {
            super("The file uploaded is not valid");
        }
    }

    public static class FileNameInvalid extends RuntimeException {
        FileNameInvalid(String fileName) {
            super("The file name is not a valid filename " + fileName);
        }
    }
}
