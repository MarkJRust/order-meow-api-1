package com.ordermeow.api.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WebSecurityTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAllowSwaggerEndpoints_withoutAuthentication() {
        assertAccess(get("/swagger-ui.html"));
        assertAccess(get("/v2/api-docs"));
    }

    @Test
    void shouldNotAllowOtherEndpoints_withoutAuthentication() {
        assertNoAccess(get("/does-not-exist"));
    }


    private ResponseEntity<?> get(String urlPath) {
        return restTemplate.getForEntity(urlPath, String.class);
    }

    private ResponseEntity<?> post(String urlPath, Object post) {
        return restTemplate.postForEntity(urlPath, post, post.getClass());
    }

    private void assertAccess(ResponseEntity<?> entity) {
        assertThat(entity.getStatusCode()).isNotIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }

    private void assertNoAccess(ResponseEntity<?> entity) {
        assertThat(entity.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN, HttpStatus.NOT_FOUND);
    }
}