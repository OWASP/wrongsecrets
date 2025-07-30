package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/**
 * Challenge with a JavaScript-based in-browser LLM that has a hidden secret in its system prompt.
 */
@Component
public class Challenge57 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return "WRONG_SECRETS_LLM_HIDDEN_INSTRUCTION_2024";
  }
}
