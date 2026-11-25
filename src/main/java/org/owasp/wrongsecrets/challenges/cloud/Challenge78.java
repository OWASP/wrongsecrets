package org.owasp.wrongsecrets.challenges.cloud;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cloud challenge in which the participant has to retrieve an ed25519 private key from a
 * deliberately public S3 backup bucket.
 *
 * <p>The application never holds the key as a literal in the sources: the expected value is
 * injected through the {@code challenge78_backup_key} property, which the deployment fills with the
 * contents of the very same fixture that is uploaded to the bucket. The key itself is only ever
 * handed out by the bucket, never by this application.
 *
 * <p>Because a multi line key loses its line breaks while being pasted into the answer box, the
 * comparison ignores line breaks, tabs and spaces on both sides.
 *
 * <p>See <a href="https://github.com/OWASP/wrongsecrets/issues/982">issue 982</a>.
 */
@Component
public class Challenge78 implements Challenge {

  private static final String UNCONFIGURED_VALUE = "if_you_see_this_please_use_AWS_Setup";

  private final String configuredKey;
  private String expectedKey;

  /**
   * Cloud challenge in which the participant has to retrieve an ed25519 private key from a
   * deliberately public S3 backup bucket.
   *
   * @param configuredKey the key which was uploaded to the backup bucket, injected by the
   *     deployment so that this class never contains the answer itself
   */
  public Challenge78(@Value("${challenge78_backup_key}") String configuredKey) {
    this.configuredKey = configuredKey;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getExpectedKey());
  }

  @Override
  public boolean answerCorrect(String answer) {
    if (Strings.isNullOrEmpty(answer)) {
      return false;
    }
    return withoutWhitespace(answer).equals(withoutWhitespace(getExpectedKey()));
  }

  private String getExpectedKey() {
    if (expectedKey == null) {
      expectedKey =
          Strings.isNullOrEmpty(configuredKey) ? UNCONFIGURED_VALUE : configuredKey.strip();
    }
    return expectedKey;
  }

  private String withoutWhitespace(String value) {
    return value.replace("\r", "").replace("\n", "").replace("\t", "").replace(" ", "");
  }
}
