package org.owasp.wrongsecrets.challenges.cloud;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AWS;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AZURE;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.GCP;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Cloud challenge that leverages the CSI secrets driver of the cloud you are running in. */
@Component
@Slf4j
@Order(10)
public class Challenge10 extends CloudChallenge {

  private final String awsDefaultValue;
  private final String challengeAnswer;

  public Challenge10(
      ScoreCard scoreCard,
      @Value("${secretmountpath}") String filePath,
      @Value("${default_aws_value_challenge_10}") String awsDefaultValue,
      @Value("${FILENAME_CHALLENGE10}") String fileName,
      RuntimeEnvironment runtimeEnvironment) {
    super(scoreCard, runtimeEnvironment);
    this.awsDefaultValue = awsDefaultValue;
    this.challengeAnswer = getCloudChallenge9and10Value(filePath, fileName);
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(challengeAnswer);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return challengeAnswer.equals(answer);
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the file is based on an Env Var")
  private String getCloudChallenge9and10Value(String filePath, String fileName) {
    try {
      return Files.readString(Paths.get(filePath, fileName));
    } catch (Exception e) {
      log.warn(
          "Exception during reading file ({}/{}}), defaulting to default without AWS",
          filePath,
          fileName);
      return awsDefaultValue;
    }
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(GCP, AWS, AZURE);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EXPERT;
  }

  /** {@inheritDoc} Uses CSI Driver */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.CSI.id;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }
}
