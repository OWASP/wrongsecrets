package org.owasp.wrongsecrets;

import org.apache.hc.client5.http.HttpHostConnectException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.*;

// Tests worked with Spring Boot 2.7.5 with random port configuration
// Not working after migration to Spring Boot 3.0
// Revert change when ticket https://github.com/spring-projects/spring-boot/issues/33451
// is resolved.
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HerokuWebSecurityConfigTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    @Test
    void shouldRedirectWhenProtoProvided() {
        var restTemplate = builder
            .defaultHeader("x-forwarded-proto", "value")
            .build();
        var rootAddress = "http://localhost:" + port + "/heroku";//note we loosely ask for "heroku" to be part of the url

        //in Spring security 2022 we no longer can bind to the new port of the redirect as that is not preset. hence this exception proves the redirect
        Exception exception = assertThrows(ResourceAccessException.class, () -> {
            restTemplate.getForEntity(rootAddress, String.class);
        });

        assertEquals(exception.getCause().getClass(), HttpHostConnectException.class);
        assertEquals(exception.getCause().getMessage(), "Connect to https://localhost:8443 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused");
    }

    @Test
    void shouldNotRedirectWhenProtoNotProvided() {
        var restTemplate = builder
            .build();
        var rootAddress = "http://localhost:" + port + "/";
        ResponseEntity<String> entity = restTemplate.getForEntity(rootAddress, String.class);
        assertTrue(entity.getStatusCode().is2xxSuccessful());
    }
}
