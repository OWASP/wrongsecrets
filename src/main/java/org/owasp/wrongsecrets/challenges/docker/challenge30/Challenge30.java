package org.owasp.wrongsecrets.challenges.docker.challenge30;

import com.google.common.base.Strings;
import java.security.SecureRandom;
import java.util.Random;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/**
 * This is a localstorage based challenge to educate people on the use of localstorage and secrets.
 */
@Component
public class Challenge30 implements Challenge {
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
}
