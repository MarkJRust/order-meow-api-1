package com.ordermeow.api.order;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {

    Optional<List<OrderEntity>> findByOrderUuid(String orderId);
}
