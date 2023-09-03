package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in comments in a front-end. */
@Slf4j
@Component
@Order(23)
public class Challenge23 extends Challenge {

  public Challenge23(ScoreCard scoreCard) {
    super(scoreCard);
  }

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

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} Frontend based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.FRONTEND.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
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
