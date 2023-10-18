package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Challenge42Test {
  @Mock private ScoreCard scoreCard;

  @Mock private Resource resource;

  @BeforeEach
  void setUp() throws IOException {
      when(resource.getContentAsString(Charset.defaultCharset()))
          .thenReturn("<root><nexus><username>test_user</username><password>test_password</password></nexus></root>");
  }

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge42(scoreCard, resource);
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge42(scoreCard, resource);
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
