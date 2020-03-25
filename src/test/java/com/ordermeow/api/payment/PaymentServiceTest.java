package com.ordermeow.api.payment;

import com.ordermeow.api.product.ProductEntity;
import com.ordermeow.api.product.ProductExceptions;
import com.ordermeow.api.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private ProductRepository productRepository;


    @Test
    public void calculateProductPrice_success() {
        List<ProductEntity> products = new ArrayList<ProductEntity>();
        BigDecimal expected = BigDecimal.ZERO;
        List<Long> productIds = new ArrayList<>();

        for (int i = 0; i < Math.random() * 10; i++) {
            ProductEntity product = createProductEntity(i, BigDecimal.valueOf(Math.random() * 100).setScale(2, BigDecimal.ROUND_CEILING));
            expected = expected.add(product.getProductPrice());
            productIds.add(product.getProductId());
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        }

        expected = expected.setScale(2, RoundingMode.CEILING);
        BigDecimal actual = paymentService.calculateTotal(productIds);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void calculateProductPrice_notFound() {
        ProductEntity productEntity = createProductEntity(1L, BigDecimal.ONE);
        when(productRepository.findById(productEntity.getProductId())).thenThrow(ProductExceptions.ProductNotFound.class);
        List<Long> productIdQuantity = new ArrayList<Long>();
        productIdQuantity.add(productEntity.getProductId());

        Assertions.assertThrows(ProductExceptions.ProductNotFound.class, () -> paymentService.calculateTotal(productIdQuantity));

    }

    private ProductEntity createProductEntity(long id, BigDecimal price) {
        ProductEntity product = new ProductEntity();
        product.setProductId(id);
        product.setProductPrice(price);
        return product;
    }
}