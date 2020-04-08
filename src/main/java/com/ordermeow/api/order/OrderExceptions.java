package com.ordermeow.api.order;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderExceptions {

    public static class OrderNotFound extends RuntimeException {
        public OrderNotFound(String orderId) {
            super("The order was not found: " + orderId);
        }
    }

    public static class DifferentTotalPrices extends RuntimeException {
        public DifferentTotalPrices(BigDecimal expectedPrice, BigDecimal actualPrice) {
            super("The expected product price: " + expectedPrice + " did not match the actual: " + actualPrice);
        }
    }

}
