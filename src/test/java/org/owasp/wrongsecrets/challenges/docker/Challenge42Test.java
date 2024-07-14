package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.challenges.docker.challenge42.AuditConfiguration;
import org.owasp.wrongsecrets.challenges.docker.challenge42.Challenge42;

@ExtendWith(MockitoExtension.class)
public class Challenge42Test {

  @Mock private AuditConfiguration auditConfiguration;

  @BeforeEach
  void init() {
    when(auditConfiguration.getApiKey()).thenReturn("qemGhPXJjmipa9O7cYBJnuO79BQg");
  }

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge42(auditConfiguration);
    var solution = challenge.spoiler().solution();
    assertEquals("qemGhPXJjmipa9O7cYBJnuO79BQg", solution);
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge42(auditConfiguration);
    assertTrue(challenge.answerCorrect("qemGhPXJjmipa9O7cYBJnuO79BQg"));
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge42(auditConfiguration);
    assertFalse(challenge.answerCorrect("wrong answer"));
  }
}
