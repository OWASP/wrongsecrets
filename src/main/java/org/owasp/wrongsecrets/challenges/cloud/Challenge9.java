package org.owasp.wrongsecrets.challenges.cloud;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Cloud challenge which focuses on Terraform and secrets. */
@Component
@Slf4j
public class Challenge9 extends FixedAnswerChallenge {

  private final String awsDefaultValue;
  private final String filePath;
  private final String fileName;

  /**
   * Cloud challenge which focuses on Terraform and secrets.
   *
   * @param filePath used to mount in the secrets store where teh actual secret lands in from the
   *     cloud
   * @param awsDefaultValue used to indicate whether a cloud setup is enabled.
   * @param fileName name of the actual secret file mounted on the filePath
   */
  public Challenge9(
      @Value("${secretmountpath}") String filePath,
      @Value("${default_aws_value_challenge_9}") String awsDefaultValue,
      @Value("${FILENAME_CHALLENGE9}") String fileName) {
    this.awsDefaultValue = awsDefaultValue;
    this.filePath = filePath;
    this.fileName = fileName;
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the fileName and filePath is based on an Env Var")
  private String getCloudChallenge9and10Value(String filePath, String fileName) {
    try {
      return Files.readString(Paths.get(filePath, fileName), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn(
          "Exception during reading file ({}/{}}), defaulting to default without AWS",
          filePath,
          fileName);
      return awsDefaultValue;
    }
  }

  @Override
  public String getAnswer() {
    return getCloudChallenge9and10Value(filePath, fileName);
  }
}
