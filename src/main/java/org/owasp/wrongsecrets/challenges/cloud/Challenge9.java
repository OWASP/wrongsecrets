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

/** Cloud challenge which focuses on Terraform and secrets. */
@Component
@Slf4j
@Order(9)
public class Challenge9 extends CloudChallenge {

  private final String awsDefaultValue;
  private final String challengeAnswer;

  /**
   * Cloud challenge which focuses on Terraform and secrets.
   *
   * @param scoreCard required for score keeping
   * @param filePath used to mount in the secrets store where teh actual secret lands in from the
   *     cloud
   * @param awsDefaultValue used to indicate whether a cloud setup is enabled.
   * @param fileName name of the actual secret file mounted on the filePath
   * @param runtimeEnvironment runtime env required to run this in.
   */
  public Challenge9(
      ScoreCard scoreCard,
      @Value("${secretmountpath}") String filePath,
      @Value("${default_aws_value_challenge_9}") String awsDefaultValue,
      @Value("${FILENAME_CHALLENGE9}") String fileName,
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
      justification = "The location of the fileName and filePath is based on an Env Var")
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
    return Difficulty.HARD;
  }

  /** {@inheritDoc} Uses Terraform */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.TERRAFORM.id;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }
}
