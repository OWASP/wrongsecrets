package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class SlackNotificationServiceTest {

  @Mock private RestClient restClient;
  @Mock private RestClient.RequestBodyUriSpec postSpec;
  @Mock private RestClient.RequestBodySpec bodySpec;
  @Mock private RestClient.ResponseSpec responseSpec;
  @Mock private Challenge59 challenge59;

  private SlackNotificationService slackNotificationService;

  @BeforeEach
  void setUp() {
    when(restClient.post()).thenReturn(postSpec);
    when(postSpec.uri(anyString())).thenReturn(bodySpec);
    when(bodySpec.contentType(any(MediaType.class))).thenReturn(bodySpec);
    when(bodySpec.body(any())).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void shouldSendNotificationWithUserAgentWhenSlackIsConfigured() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    String userAgent = "Mozilla/5.0 (Test Browser)";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser", userAgent);

    // Then
    verify(restClient, times(1)).post();
    verify(postSpec, times(1)).uri(webhookUrl);
  }

  @Test
  void shouldIncludeUserAgentInMessageWhenProvided() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    String userAgent = "Cypress WrongSecrets E2E Tests";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser", userAgent);

    // Then
    ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
    verify(bodySpec).body(bodyCaptor.capture());
    SlackNotificationService.SlackMessage slackMessage =
        (SlackNotificationService.SlackMessage) bodyCaptor.getValue();
    assertTrue(slackMessage.text().contains("(User-Agent: " + userAgent + ")"));
  }

  @Test
  void shouldNotIncludeUserAgentInMessageWhenNotProvided() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser", null);

    // Then
    ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
    verify(bodySpec).body(bodyCaptor.capture());
    SlackNotificationService.SlackMessage slackMessage =
        (SlackNotificationService.SlackMessage) bodyCaptor.getValue();
    assertFalse(slackMessage.text().contains("User-Agent"));
  }

  @Test
  void shouldSendNotificationWhenSlackIsConfigured() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restClient, times(1)).post();
  }

  @Test
  void shouldNotSendNotificationWhenSlackNotConfigured() {
    // Given
    slackNotificationService = new SlackNotificationService(restClient, null);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restClient, never()).post();
  }

  @Test
  void shouldNotSendNotificationWhenWebhookUrlNotSet() {
    // Given
    when(challenge59.getSlackWebhookUrl()).thenReturn("not_set");
    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restClient, never()).post();
  }

  @Test
  void shouldNotSendNotificationWhenWebhookUrlIsInvalid() {
    // Given
    when(challenge59.getSlackWebhookUrl()).thenReturn("https://example.com/invalid");
    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser");

    // Then
    verify(restClient, never()).post();
  }

  @Test
  void shouldHandleRestClientException() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(responseSpec.toEntity(String.class)).thenThrow(new RuntimeException("Network error"));

    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When & Then - should not throw exception
    assertDoesNotThrow(
        () -> slackNotificationService.notifyChallengeCompletion("challenge-1", "testuser"));
  }

  @Test
  void shouldSendNotificationWithNullUsername() {
    // Given
    String webhookUrl = "https://hooks.slack.com/services/T123456789/B123456789/abcdef123456";
    when(challenge59.getSlackWebhookUrl()).thenReturn(webhookUrl);
    when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("ok"));

    slackNotificationService = new SlackNotificationService(restClient, challenge59);

    // When
    slackNotificationService.notifyChallengeCompletion("challenge-1", null);

    // Then
    verify(restClient, times(1)).post();
  }
}
