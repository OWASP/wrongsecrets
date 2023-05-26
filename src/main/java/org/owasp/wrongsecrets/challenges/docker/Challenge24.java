package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about using a publicly specified key to safeguard data. */
@Slf4j
@Component
@Order(24)
public class Challenge24 extends Challenge {

  public Challenge24(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getActualData());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return getActualData().equals(answer);
  }

  @Override
  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} Cryptography based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.CRYPTOGRAPHY.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  public String getActualData() {
    return new String(
        Hex.decode(
            "3030303130323033203034303530363037203038303930413042203043304430453046203130313131323133203134313531363137203138313931413142203143314431453146203230323132323233203234323532363237203238323932413242203243324432453246203330333133323333203334333533363337203338333933413342203343334433453346"
                .getBytes(StandardCharsets.UTF_8)),
        StandardCharsets.UTF_8);
  }
}
