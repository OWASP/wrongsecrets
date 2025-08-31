package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class Challenge59Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var restTemplate = mock(RestTemplate.class);
    // Mock to avoid any real API calls
    when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(null);
    var challenge = new Challenge59(restTemplate);

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("telegram_secret_found_in_channel"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var restTemplate = mock(RestTemplate.class);
    // Mock to avoid any real API calls
    when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(null);
    var challenge = new Challenge59(restTemplate);

    assertThat(challenge.answerCorrect("telegram_secret_found_in_channel")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var restTemplate = mock(RestTemplate.class);
    // Mock to avoid any real API calls
    when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(null);
    var challenge = new Challenge59(restTemplate);

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }

  @Test
  void shouldReturnSecretWhenTelegramApiSucceeds() {
    var restTemplate = mock(RestTemplate.class);
    var challenge = new Challenge59(restTemplate);

    // Mock successful API response
    Map<String, Object> mockResponse = Map.of("ok", true);
    when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(mockResponse);

    assertThat(challenge.spoiler().solution()).isEqualTo("telegram_secret_found_in_channel");
  }

  @Test
  void shouldReturnFallbackSecretWhenTelegramApiFails() {
    var restTemplate = mock(RestTemplate.class);
    var challenge = new Challenge59(restTemplate);

    // Mock API failure
    when(restTemplate.getForObject(any(String.class), eq(Map.class)))
        .thenThrow(new RestClientException("Network error"));

    assertThat(challenge.spoiler().solution()).isEqualTo("telegram_secret_found_in_channel");
  }
}
