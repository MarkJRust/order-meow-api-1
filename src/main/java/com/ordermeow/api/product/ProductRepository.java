package com.ordermeow.api.product;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {

    @Transactional
    void deleteByProductId(Long productId);

}
