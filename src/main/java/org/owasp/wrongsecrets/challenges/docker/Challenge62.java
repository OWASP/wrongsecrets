package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Challenge demonstrating how an MCP server with an overly-privileged Google Service Account can be
 * used to escalate privileges and access Google Drive documents that the caller is not authorized
 * to read directly.
 */
@Component
public class Challenge62 implements Challenge {

  private final String googleDriveSecret;

  public Challenge62(
      @Value(
              "${WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET:if_you_see_this_configure_the_google_service_account_properly}")
          String googleDriveSecret) {
    this.googleDriveSecret = googleDriveSecret;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(googleDriveSecret);
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer) && googleDriveSecret.equals(answer.trim());
  }
}
