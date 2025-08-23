package org.owasp.wrongsecrets.challenges.docker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  private final String slackWebhookUrl;

  public SlackNotificationService(
      RestTemplate restTemplate,
      ObjectMapper objectMapper,
      @Autowired(required = false) Challenge59 challenge59,
      @Value("${SLACK_WEBHOOK_URL:}") String slackWebhookUrl) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.challenge59 = Optional.ofNullable(challenge59);
    this.slackWebhookUrl = slackWebhookUrl;
  }

  /**
   * Sends a Slack notification when a challenge is completed.
   *
   * @param challengeName The name of the completed challenge
   * @param userName Optional username of the person who completed the challenge
   */
  public void notifyChallengeCompletion(String challengeName, String userName) {
    if (!isSlackConfigured()) {
      logger.debug("Slack not configured, skipping notification for challenge: {}", challengeName);
      return;
    }

    try {
      String message = buildCompletionMessage(challengeName, userName);
      SlackMessage slackMessage = new SlackMessage(message);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      
      if (challenge59.isPresent()) {
        headers.setBearerAuth(challenge59.get().getSlackKey());
      }

      HttpEntity<SlackMessage> request = new HttpEntity<>(slackMessage, headers);

      restTemplate.postForEntity(slackWebhookUrl, request, String.class);
      logger.info("Successfully sent Slack notification for challenge completion: {}", challengeName);

    } catch (Exception e) {
      logger.warn("Failed to send Slack notification for challenge: {}", challengeName, e);
    }
  }

  private boolean isSlackConfigured() {
    return challenge59.isPresent() 
        && slackWebhookUrl != null 
        && !slackWebhookUrl.trim().isEmpty()
        && !slackWebhookUrl.equals("not_set");
  }

  private String buildCompletionMessage(String challengeName, String userName) {
    String userPart = (userName != null && !userName.trim().isEmpty()) 
        ? " by " + userName 
        : "";
    
    return String.format(
        "🎉 Challenge %s completed%s! Another secret vulnerability discovered in WrongSecrets.",
        challengeName, userPart);
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