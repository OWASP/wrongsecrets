package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a C++ binary. */
@Component
@Order(20)
@Slf4j
public class Challenge20 extends Challenge {

  private final BinaryExecutionHelper binaryExecutionHelper;

  public Challenge20(ScoreCard scoreCard) {
    super(scoreCard);
    this.binaryExecutionHelper = new BinaryExecutionHelper(20, new MuslDetectorImpl());
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(binaryExecutionHelper.executeCommand("", "wrongsecrets-cplus"));
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return binaryExecutionHelper
        .executeCommand(answer, "wrongsecrets-cplus")
        .equals("This is correct! Congrats!");
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EXPERT;
  }

  /** {@inheritDoc} Binary based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.BINARY.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }
}
