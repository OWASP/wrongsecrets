package org.owasp.wrongsecrets.challenges.docker;

import com.google.api.client.util.Strings;
import java.security.SecureRandom;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge which leaks the data in the logs instead of anywhere else. */
@Slf4j
@Component
public class Challenge8 implements Challenge {

  private static final String alphabet =
      "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

  private final Random secureRandom = new SecureRandom();
  private final String randomValue;

  public Challenge8(@Value("${challenge_acht_ctf_host_value}") String serverCode) {
    if (!Strings.isNullOrEmpty(serverCode) && !serverCode.equals("not_set")) {
      randomValue = serverCode;
    } else {
      randomValue = generateRandomString();
    }
    log.info("Initializing challenge 8 with random value {}", randomValue);
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

  private String generateRandomString() {
    final int length = 10;
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
    }
    return new String(builder);
  }
}
