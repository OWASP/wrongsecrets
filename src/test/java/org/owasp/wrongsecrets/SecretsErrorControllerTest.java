package org.owasp.wrongsecrets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecretsErrorControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    @Test
    void shouldReturnErrorPage() {
        var restTemplate = builder.build();
        var unknownChallenge = "http://localhost:" + port + "/challenge/999";
        try {
            restTemplate.getForEntity(unknownChallenge, String.class);
            fail();
        } catch (RestClientResponseException e) {
           assertThat(e.getResponseBodyAsString())
               .contains("WHOOOOPS")
               .contains("Something went wrong!");
        }
    }
}
