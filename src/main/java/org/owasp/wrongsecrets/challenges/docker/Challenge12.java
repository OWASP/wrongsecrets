package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
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

/** Challenge focused on filesystem issues in docker container due to workdir copying. */
@Slf4j
@Component
@Order(12)
public class Challenge12 extends Challenge {

  private final String dockerMountPath;

  public Challenge12(
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
    // log.debug("challenge 12, actualdata: {}, answer: {}", getActualData(), answer);
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

  /** {@inheritDoc} Docker based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.DOCKER.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the dockerMountPath is based on an Env Var")
  private String getActualData() {
    try {
      return Files.readString(Paths.get(dockerMountPath, "yourkey.txt"), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception during file reading, defaulting to default without cloud environment", e);
      return "if_you_see_this_please_use_docker_instead";
    }
  }
}
