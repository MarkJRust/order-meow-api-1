package com.ordermeow.api.order;

import com.ordermeow.api.product.ProductEntity;
import com.ordermeow.api.product.ProductExceptions;
import com.ordermeow.api.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;


    @Test
    public void calculateProductPrice_success() {
        BigDecimal expected = BigDecimal.ZERO;
        List<Long> productIds = new ArrayList<>();

        for (int i = 0; i < Math.random() * 10; i++) {
            ProductEntity product = createProductEntity(i, BigDecimal.valueOf(Math.random() * 100).setScale(2, BigDecimal.ROUND_CEILING));
            expected = expected.add(product.getProductPrice());
            productIds.add(product.getProductId());
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        }

        expected = expected.setScale(2, RoundingMode.CEILING);
        BigDecimal actual = orderService.calculateTotal(productIds);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    void calculateProductPrice_notFound() {
        ProductEntity productEntity = createProductEntity(1L, BigDecimal.ONE);
        when(productRepository.findById(productEntity.getProductId())).thenThrow(ProductExceptions.ProductNotFound.class);
        List<Long> productIdQuantity = new ArrayList<>();
        productIdQuantity.add(productEntity.getProductId());

        Assertions.assertThrows(ProductExceptions.ProductNotFound.class, () -> orderService.calculateTotal(productIdQuantity));
    }

    @Test
    void createOrder_notFound() {
        List<Long> productIds = Collections.singletonList(1L);

        when(productRepository.findById(productIds.get(0))).thenReturn(Optional.empty());
        Assertions.assertThrows(ProductExceptions.ProductNotFound.class, () -> orderService.createOrder(productIds, null));
    }

    @Test
    void createOrder_totalsNotEqual() {
        List<Long> productIds = Collections.singletonList(1L);
        BigDecimal expectedCost = BigDecimal.ONE;

        ProductEntity product = createProductEntity(1L, BigDecimal.TEN);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Assertions.assertThrows(OrderExceptions.DifferentTotalPrices.class, () -> orderService.createOrder(productIds, expectedCost));
    }

    @Test
    void createOrder_success() {
        List<Long> productIds = Arrays.asList(1L, 2L);
        List<ProductEntity> productEntities = new ArrayList<>();
        productEntities.add(createProductEntity(1L, BigDecimal.TEN));
        productEntities.add(createProductEntity(2L, BigDecimal.ZERO));
        BigDecimal expectedCost = BigDecimal.TEN;

        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntities.get(0)));
        when(productRepository.findById(2L)).thenReturn(Optional.of(productEntities.get(1)));
        when(orderRepository.save(Mockito.any(OrderEntity.class))).thenAnswer(ret -> ret.getArgument(0));

        List<OrderEntity> order = orderService.createOrder(productIds, expectedCost);
        Assertions.assertEquals(productEntities.size(), order.size());
        verify(orderRepository, times(productEntities.size())).save(Mockito.any(OrderEntity.class));
    }

    @Test
    void getOrder_notFound() {
        UUID notFoundUUId = UUID.randomUUID();

        when(orderRepository.findByOrderUuid(notFoundUUId.toString())).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderExceptions.OrderNotFound.class, () -> orderService.getOrder(notFoundUUId.toString()));
    }

    @Test
    void getOrder_success() {
        UUID orderUuid = UUID.randomUUID();
        List<OrderEntity> order = new ArrayList<>();
        ProductEntity productEntity = createProductEntity(1L, BigDecimal.ONE);

        for (int i = 0; i < 10; i++) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderUuid(orderUuid.toString());
            orderEntity.setProductPrice(productEntity.getProductPrice());
            orderEntity.setProductName(productEntity.getProductName());
            order.add(orderEntity);
        }

        when(orderRepository.findByOrderUuid(orderUuid.toString())).thenReturn(Optional.of(order));
        Assertions.assertEquals(order.size(), orderService.getOrder(orderUuid.toString()).size());
    }

    private ProductEntity createProductEntity(long id, BigDecimal price) {
        ProductEntity product = new ProductEntity();
        product.setProductId(id);
        product.setProductPrice(price);
        return product;
    }
}