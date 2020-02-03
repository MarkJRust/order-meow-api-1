package com.ordermeow.api.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@EnableJpaAuditing
class UserRepositoryTest {

    private static UserEntity userEntity;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @BeforeAll
    public static void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("titus");
        userEntity.setPassword("titusr0ckz");
    }

    @Test
    public void findByUsernameReturnsUserEntity() {
        UserEntity user = em.persistAndFlush(userEntity);
        assertThat(userRepository.findUserEntityByUsername(userEntity.getUsername())).contains(user);
    }
}