package org.owasp.wrongsecrets.challenges.cloud;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cloud challenge which leaks a Base64 encoded secret into the log stream of the cloud provider.
 *
 * <p>The application never exposes the secret through an endpoint, a file or an environment
 * variable: it only writes the encoded value to standard out. The logging agent of the cloud
 * provider (CloudWatch Logs on AWS, Cloud Logging on GCP, Log Analytics on Azure) ships that line
 * to the central log sink, so the log sink is the only place where the secret can be retrieved.
 *
 * <p>Note the difference with {@code Challenge8}: that challenge logs the answer in plain text and
 * is solvable from local container logs. Here the value is encoded first, and the challenge is only
 * offered in the cloud environments.
 *
 * <p>See <a href="https://github.com/OWASP/wrongsecrets/issues/345">issue 345</a>.
 */
@Slf4j
@Component
public class Challenge67 extends FixedAnswerChallenge {

  private static final String NOT_SET = "not_set";
  private static final String MDC_CHALLENGE_KEY = "wrongsecrets.challenge";
  private static final String MDC_PAYLOAD_KEY = "audit.payload";
  private static final String ALPHABET =
      "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
  private static final int GENERATED_SECRET_LENGTH = 16;

  private final SecureRandom secureRandom = new SecureRandom();
  private final String configuredSecret;

  /**
   * Cloud challenge which leaks a Base64 encoded secret towards the log sink of the cloud provider.
   *
   * @param configuredSecret the secret injected by the cloud deployment; when it is absent a random
   *     secret is generated so that every boot still has a unique answer instead of a value that
   *     can be read from this repository
   */
  public Challenge67(@Value("${challenge67_cloud_log_secret}") String configuredSecret) {
    this.configuredSecret = configuredSecret;
  }

  @Override
  public String getAnswer() {
    String secret = resolveSecret();
    leakSecretToCloudLogging(secret);
    return secret;
  }

  private String resolveSecret() {
    if (configuredSecret == null
        || configuredSecret.isBlank()
        || NOT_SET.equals(configuredSecret)) {
      return generateRandomSecret();
    }
    return configuredSecret;
  }

  private String generateRandomSecret() {
    StringBuilder builder = new StringBuilder(GENERATED_SECRET_LENGTH);
    for (int i = 0; i < GENERATED_SECRET_LENGTH; i++) {
      builder.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
    }
    return builder.toString();
  }

  /**
   * Writes the Base64 encoded secret to the log stream, both inside the message and as a structured
   * MDC field. Shipping an "audit event" with the raw payload attached is a realistic way for a
   * credential to end up in a cloud log sink without anybody noticing.
   *
   * @param secret the plain text secret which is the answer to this challenge
   */
  private void leakSecretToCloudLogging(String secret) {
    String encodedSecret =
        Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
    MDC.put(MDC_CHALLENGE_KEY, "challenge-67");
    MDC.put(MDC_PAYLOAD_KEY, encodedSecret);
    try {
      log.info(
          "Shipping audit event to the cloud logging sink, encoded credential: {}", encodedSecret);
    } finally {
      MDC.remove(MDC_CHALLENGE_KEY);
      MDC.remove(MDC_PAYLOAD_KEY);
    }
  }
}
