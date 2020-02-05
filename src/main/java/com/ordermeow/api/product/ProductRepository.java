package com.ordermeow.api.product;

import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {
}
