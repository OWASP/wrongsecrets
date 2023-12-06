package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in comments in a front-end. */
@Slf4j
@Component
public class Challenge23 implements Challenge {

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getActualData());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    // log.debug("challenge 23, actualdata: {}, answer: {}", getActualData(), answer);
    return getActualData().equals(answer);
  }

  public String getActualData() {
    return new String(
        Base64.decode(
            Hex.decode(
                Base64.decode(
                    "NTYzMjY4MzU1MTMyMzk3NDYyNTc1Njc1NjQ0ODRlNDI2MzMxNDI2ODYzMzM0ZTdhNjQzMjM5Nzk1YTQ1NDY3OTVhNTU0YTY4NWE0NDRkMzA0ZTU2Mzg2Yg=="))),
        StandardCharsets.UTF_8);
  }
}
