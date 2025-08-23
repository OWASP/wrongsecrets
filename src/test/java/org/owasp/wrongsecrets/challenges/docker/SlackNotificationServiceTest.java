package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class SlackNotificationServiceTest {

  @Mock private RestTemplate restTemplate;
  @Mock private Challenge59 challenge59;

  private ObjectMapper objectMapper;
  private SlackNotificationService slackNotificationService;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldSendNotificationWhenSlackIsConfigured() {
    // Given
    when(challenge59.getSlackKey()).thenReturn("xoxb-test-token");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService =
        new SlackNotificationService(
            restTemplate, objectMapper, challenge59, "https://hooks.slack.com/webhook");

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, times(1))
        .postForEntity(eq("https://hooks.slack.com/webhook"), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void shouldNotSendNotificationWhenSlackNotConfigured() {
    // Given
    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, null, "");

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, never()).postForEntity(anyString(), any(), any());
  }

  @Test
  void shouldNotSendNotificationWhenWebhookUrlNotSet() {
    // Given
    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, challenge59, "not_set");

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, never()).postForEntity(anyString(), any(), any());
  }

  @Test
  void shouldHandleRestTemplateException() {
    // Given
    when(challenge59.getSlackKey()).thenReturn("xoxb-test-token");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Network error"));

    slackNotificationService =
        new SlackNotificationService(
            restTemplate, objectMapper, challenge59, "https://hooks.slack.com/webhook");

    // When & Then - should not throw exception
    assertDoesNotThrow(
        () -> slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser"));
  }

  @Test
  void shouldSendNotificationWithNullUsername() {
    // Given
    when(challenge59.getSlackKey()).thenReturn("xoxb-test-token");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService =
        new SlackNotificationService(
            restTemplate, objectMapper, challenge59, "https://hooks.slack.com/webhook");

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", null);

    // Then
    verify(restTemplate, times(1))
        .postForEntity(eq("https://hooks.slack.com/webhook"), any(HttpEntity.class), eq(String.class));
  }
}