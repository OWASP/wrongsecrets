package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import java.io.IOException;
import java.nio.charset.Charset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

@ExtendWith(MockitoExtension.class)
class Challenge39Test {
  @Mock private Resource resource;

  @BeforeEach
  void setUp() throws IOException {
    when(resource.getContentAsString(Charset.defaultCharset()))
        .thenReturn("YHh0aLDtW6WjFwlOgDrcRELRBIaAW9IUqR6sPy8NyYQ=");
    when(resource.getFilename()).thenReturn("secrchallenge.md");
  }

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge39(resource);
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    assertThat(challenge.spoiler().solution()).isNotEqualTo(DECRYPTION_ERROR);
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge39(resource);
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
