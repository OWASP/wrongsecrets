package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about having a secret obfuscated in the front-end code. */
@Slf4j
@Component
@Order(16)
public class Challenge16 extends Challenge {

  private final String dockerMountPath;

  public Challenge16(
      ScoreCard scoreCard, @Value("${challengedockermtpath}") String dockerMountPath) {
    super(scoreCard);
    this.dockerMountPath = dockerMountPath;
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
    // log.debug("challenge 16, actualdata: {}, answer: {}", getActualData(), answer);
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
    return Difficulty.HARD;
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

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the dockerMountPath is based on an Env Var")
  public String getActualData() {
    try {
      return Files.readString(Paths.get(dockerMountPath, "secondkey.txt"));
    } catch (Exception e) {
      log.warn("Exception during file reading, defaulting to default without cloud environment", e);
      return "if_you_see_this_please_use_docker_instead";
    }
  }
}
