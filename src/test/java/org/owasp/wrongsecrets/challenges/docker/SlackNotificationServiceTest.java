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
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, times(1))
        .postForEntity(eq(webhookUrl), any(HttpEntity.class), eq(String.class));
  }

  @Test
  void shouldNotSendNotificationWhenSlackNotConfigured() {
    // Given
    slackNotificationService = new SlackNotificationService(restTemplate, objectMapper, null);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, never()).postForEntity(anyString(), any(), any());
  }

  @Test
  void shouldNotSendNotificationWhenWebhookUrlNotSet() {
    // Given
    when(challenge59.getSlackWebhookUrl()).thenReturn("not_set");
    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, never()).postForEntity(anyString(), any(), any());
  }

  @Test
  void shouldNotSendNotificationWhenWebhookUrlIsInvalid() {
    // Given
    when(challenge59.getSlackWebhookUrl()).thenReturn("https://example.com/invalid");
    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restTemplate, never()).postForEntity(anyString(), any(), any());
  }

  @Test
  void shouldHandleRestTemplateException() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RuntimeException("Network error"));

    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, challenge59);

    // When & Then - should not throw exception
    assertDoesNotThrow(
        () -> slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser"));
  }

  @Test
  void shouldSendNotificationWithNullUsername() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService =
        new SlackNotificationService(restTemplate, objectMapper, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", null);

    // Then
    verify(restTemplate, times(1))
        .postForEntity(eq(webhookUrl), any(HttpEntity.class), eq(String.class));
  }
}
