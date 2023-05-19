package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This is a localstorage based challenge to educate people on the use of localstorage and secrets.
 */
@Component
@Order(30)
public class Challenge30 extends Challenge {
  private final Random secureRandom = new SecureRandom();
  private static final String alphabet =
      "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
  private String solution;

  private String generateRandomString(int length) {
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
    }
    return new String(builder);
  }

  public Challenge30(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
    if (Strings.isNullOrEmpty(solution)) {
      solution = generateRandomString(12);
    }
    return new Spoiler(solution);
  }

  @Override
  public boolean answerCorrect(String answer) {
    if (Strings.isNullOrEmpty(solution)) {
      solution = generateRandomString(12);
    }
    return solution.equals(answer);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} This is a front-end / web type of challenge */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.FRONTEND.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }
}
