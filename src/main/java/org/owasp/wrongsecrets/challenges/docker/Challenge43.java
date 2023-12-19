package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.bouncycastle.util.encoders.Base32;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in a Reddit post. */
@Component
public class Challenge43 implements Challenge {

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSecretKey());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return getSecretKey().equals(answer);
  }

  private String getSecretKey() {
    return new String(
        Base32.decode(new String(Base64.decode("SU5FRkVTS1RLUkdVQ1VaU0pNWkRHPT09"), UTF_8)), UTF_8);
  }
}
