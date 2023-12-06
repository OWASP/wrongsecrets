package org.owasp.wrongsecrets.challenges.docker;

import static org.mockito.Mockito.when;

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
    Assertions.assertThat(challenge.answerCorrect("error_decryption")).isFalse();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge39(resource);
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
