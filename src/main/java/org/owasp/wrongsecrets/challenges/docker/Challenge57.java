package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/**
 * Challenge with a JavaScript-based in-browser LLM that has a hidden secret in its system prompt.
 */
@Component
public class Challenge57 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return getActualData();
  }

  private String getActualData() {
    return new String(
        Base64.getDecoder().decode("V1JPTkdfU0VDUkVUU19MTE1fSElEREVOX0lOU1RSVUNUSU9OXzIwMjQ="),
        StandardCharsets.UTF_8);
  }
}
