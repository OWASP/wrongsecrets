package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

import com.google.api.client.util.Strings;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Challenge which leaks the data in the logs instead of anywhere else. */
@Slf4j
@Component
@Order(8)
public class Challenge8 extends Challenge {

  private static final String alphabet =
      "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

  private final Random secureRandom = new SecureRandom();
  private final String randomValue;

  public Challenge8(
      ScoreCard scoreCard, @Value("${challenge_acht_ctf_host_value}") String serverCode) {
    super(scoreCard);
    if (!Strings.isNullOrEmpty(serverCode) && !serverCode.equals("not_set")) {
      randomValue = serverCode;
    } else {
      randomValue = generateRandomString();
    }
    log.info("Initializing challenge 8 with random value {}", randomValue);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(randomValue);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return randomValue.equals(answer);
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} Challenge is wrapped around logging */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.LOGGING.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return true;
  }

  private String generateRandomString() {
    final int length = 10;
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
    }
    return new String(builder);
  }
}
