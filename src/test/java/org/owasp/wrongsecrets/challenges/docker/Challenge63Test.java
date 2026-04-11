package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.docker.challenge63.Challenge63;

class Challenge63Test {

  @Test
  void testAnswerIsCorrect() {
    Challenge63 challenge = new Challenge63();
    assertTrue(challenge.answerCorrect("wh0sp1ay1ngwrongs3cr3ts"));
  }

  @Test
  void testWrongAnswerIsRejected() {
    Challenge63 challenge = new Challenge63();
    assertFalse(challenge.answerCorrect("wronganswer"));
  }

  @Test
  void testSpoilerRevealsAnswer() {
    Challenge63 challenge = new Challenge63();
    assertEquals("wh0sp1ay1ngwrongs3cr3ts", challenge.spoiler().solution());
  }
}
