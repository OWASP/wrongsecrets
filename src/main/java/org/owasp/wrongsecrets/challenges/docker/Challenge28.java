package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in a Github issue. */
@Component
public class Challenge28 extends FixedAnswerChallenge {

  private String getSecretKey() {
    return new String(
        Base64.decode(
            new String(
                Base64.decode("WVhOa1ptUndkVmxWU1dGa1ltRnZZWE5rY0dFd04ydHFNakF3TXc9PQ=="), UTF_8)),
        UTF_8);
  }

  @Override
  public String getAnswer() {
    return getSecretKey();
  }
}
