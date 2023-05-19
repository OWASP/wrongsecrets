package org.owasp.wrongsecrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatsControllerTests {
  @LocalServerPort private int port;

  @Autowired private RestTemplateBuilder builder;

  public StatsControllerTests() {}

  @Test
  void shouldGetStats() {
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
