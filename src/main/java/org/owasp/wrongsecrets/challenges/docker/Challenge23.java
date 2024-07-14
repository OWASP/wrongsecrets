package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in comments in a front-end. */
@Slf4j
@Component
public class Challenge23 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return getActualData();
  }

  private String getActualData() {
    return new String(
        Base64.decode(
            Hex.decode(
                Base64.decode(
                    "NTYzMjY4MzU1MTMyMzk3NDYyNTc1Njc1NjQ0ODRlNDI2MzMxNDI2ODYzMzM0ZTdhNjQzMjM5Nzk1YTQ1NDY3OTVhNTU0YTY4NWE0NDRkMzA0ZTU2Mzg2Yg=="))),
        StandardCharsets.UTF_8);
  }
}
