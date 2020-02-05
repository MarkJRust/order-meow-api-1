package com.ordermeow.api.user;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);
}
