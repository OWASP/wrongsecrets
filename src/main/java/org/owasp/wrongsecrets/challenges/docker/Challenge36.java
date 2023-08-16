package org.owasp.wrongsecrets.challenges.docker;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This is a challenge based on the idea of leaking a secret trough a vulnerability report. */
@Slf4j
@Component
@Order(36)
public class Challenge36 extends Challenge {

  public Challenge36(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getKey());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getKey().equals(answer);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} This is a crypto Documentation type of challenge */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.DOCUMENTATION.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  private String getKey() {
    // google api key
    return "AIzaSyBSpHvt8l1f9qlppJqQW280vGacXgwNnrk";
  }
}
