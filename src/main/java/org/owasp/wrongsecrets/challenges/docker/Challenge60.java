package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Challenge demonstrating how an insecure MCP server exposes secrets from environment variables.
 */
@Component
public class Challenge60 implements Challenge {

  private final String mcpSecret;

  public Challenge60(
      @Value("${WRONGSECRETS_MCP_SECRET:if_you_see_this_please_call_the_mcp_endpoint}")
          String mcpSecret) {
    this.mcpSecret = mcpSecret;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(mcpSecret);
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer) && mcpSecret.equals(answer.trim());
  }
}
