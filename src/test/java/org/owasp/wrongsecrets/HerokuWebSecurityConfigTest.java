package org.owasp.wrongsecrets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HerokuWebSecurityConfigTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    @Test
    void shouldRedirectwhenProtoProvided() {
        try {
            var restTemplate = builder
                .defaultHeader("x-forwarded-proto", "value")
                .build();
            var rootAddress = "http://localhost:" + port + "/";
            restTemplate.getForEntity(rootAddress, String.class);
            fail();
        } catch (ResourceAccessException e) {
            assert (e.getCause().getCause().toString()).contains("Redirect");
        }
    }

    @Test
    void shouldNotRedirectwhenProtoNotProvided() {
        var restTemplate = builder
            .build();
        var rootAddress = "http://localhost:" + port + "/";
        ResponseEntity entity = restTemplate.getForEntity(rootAddress, String.class);
        assertTrue(entity.getStatusCode().is2xxSuccessful());
    }
}
