package org.owasp.wrongsecrets.challenges.docker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Service for sending Slack notifications when challenges are completed. */
@Service
public class SlackNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(SlackNotificationService.class);

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final Optional<Challenge59> challenge59;

  public SlackNotificationService(
      RestTemplate restTemplate,
      ObjectMapper objectMapper,
      @Autowired(required = false) Challenge59 challenge59) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.challenge59 = Optional.ofNullable(challenge59);
  }

  /**
   * Sends a Slack notification when a challenge is completed.
   *
   * @param challengeName The name of the completed challenge
   * @param userName Optional username of the person who completed the challenge
   * @param userAgent Optional user agent string from the HTTP request
   */
  public void notifyChallengeCompletion(String challengeName, String userName, String userAgent) {
    if (!isSlackConfigured()) {
      logger.debug("Slack not configured, skipping notification for challenge: {}", challengeName);
      return;
    }

    try {
      String message = buildCompletionMessage(challengeName, userName, userAgent);
      SlackMessage slackMessage = new SlackMessage(message);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<SlackMessage> request = new HttpEntity<>(slackMessage, headers);

      String webhookUrl = challenge59.get().getSlackWebhookUrl();
      restTemplate.postForEntity(webhookUrl, request, String.class);
      logger.info(
          "Successfully sent Slack notification for challenge completion: {}", challengeName);

    } catch (Exception e) {
      logger.warn("Failed to send Slack notification for challenge: {}", challengeName, e);
    }
  }

  /**
   * Sends a Slack notification when a challenge is completed (backward compatibility method).
   *
   * @param challengeName The name of the completed challenge
   * @param userName Optional username of the person who completed the challenge
   */
  public void notifyChallengeCompletion(String challengeName, String userName) {
    notifyChallengeCompletion(challengeName, userName, null);
  }

  private boolean isSlackConfigured() {
    return challenge59.isPresent()
        && challenge59.get().getSlackWebhookUrl() != null
        && !challenge59.get().getSlackWebhookUrl().trim().isEmpty()
        && !challenge59.get().getSlackWebhookUrl().equals("not_set")
        && challenge59.get().getSlackWebhookUrl().startsWith("https://hooks.slack.com");
  }

  private String buildCompletionMessage(String challengeName, String userName, String userAgent) {
    String userPart = (userName != null && !userName.trim().isEmpty()) ? " by " + userName : "";
    String userAgentPart = (userAgent != null && !userAgent.trim().isEmpty()) ? " (User-Agent: " + userAgent + ")" : "";

    return String.format(
        "🎉 Challenge %s completed%s%s! Another secret vulnerability discovered in WrongSecrets.",
        challengeName, userPart, userAgentPart);
  }

  private String buildCompletionMessage(String challengeName, String userName) {
    return buildCompletionMessage(challengeName, userName, null);
  }

  /** Simple record for Slack message payload. */
  public static class SlackMessage {
    @JsonProperty("text")
    private final String text;

    public SlackMessage(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }
  }
}
