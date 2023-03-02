package org.owasp.wrongsecrets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.canaries.CanaryCounter;
import org.owasp.wrongsecrets.canaries.CanaryCounterImpl;
import org.owasp.wrongsecrets.challenges.ChallengeUI;
import org.owasp.wrongsecrets.challenges.ChallengesController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatsControllerTests {
    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateBuilder builder;

    public StatsControllerTests() {
    }
    @Test
    void shouldGetStats(){
        var restTemplate = builder.build();

        var callbackAdress = "http://localhost:" + port + "/stats";
        try {
            var response = restTemplate.getForEntity(callbackAdress, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("Number of canary callbacks since boot:");
        } catch (RestClientResponseException e) {
            fail(e);
        }
    }
}
