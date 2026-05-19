package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges.ErrorResponses;

class Challenge63Test {

  @Test
  void testAnswerIsCorrect() {
    Challenge63 challenge = new Challenge63();
    assertTrue(challenge.answerCorrect(challenge.getAnswer()));
    assertFalse(challenge.answerCorrect(ErrorResponses.DECRYPTION_ERROR));
  }

  @Test
  void testWrongAnswerIsRejected() {
    Challenge63 challenge = new Challenge63();
    assertFalse(challenge.answerCorrect("wronganswer"));
  }
}
