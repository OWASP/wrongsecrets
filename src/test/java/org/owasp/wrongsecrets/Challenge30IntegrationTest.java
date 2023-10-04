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
class Challenge30IntegrationTest {
  @LocalServerPort private int port;

  @Autowired private RestTemplateBuilder builder;

  public Challenge30IntegrationTest() {}

  @Test
  void shouldGetFullintegratedStepsForChallenge30() {
    var restTemplate = builder.build();

    var secretLoadingAdres = "http://localhost:" + port + "/hidden";
    var spoilAddress = "http://localhost:" + port + "/spoil-30";
    try {
      var response = restTemplate.getForEntity(secretLoadingAdres, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      String answer = response.getBody();
      var spoilResponse = restTemplate.getForEntity(spoilAddress, String.class);
      assertThat(spoilResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(spoilResponse.getBody()).contains(answer);
    } catch (RestClientResponseException e) {
      fail(e);
    }
  }
}
