package org.owasp.wrongsecrets.canaries;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CanaryCallbackTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    @Test
    void shouldAcceptPostOfMessage() {
        var restTemplate = builder.build();
        var additonalCanaryData = new AdditionalCanaryData("soruce", "agent", "referer", "locatoin");
        CanaryToken token = new CanaryToken("url", "memo", "channel", "time", additonalCanaryData);

        var callbackAdress = "http://localhost:"+port+"/canaries/tokencallback";

        try {
            var response = restTemplate.postForEntity(callbackAdress, token, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        } catch (RestClientResponseException e) {
           fail(e);
        }
    }
}
