package org.owasp.wrongsecrets.challenges.docker;

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
class Challenge40Test {
  @Mock private Resource resource;

  @BeforeEach
  void setUp() throws IOException {
    when(resource.getContentAsString(Charset.defaultCharset()))
        .thenReturn("{ \"key\": \"sample_key123456\", \"secret\": \"8uVivR1M4yra5714Z58MKQ==\"}");
  }

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge40(resource);
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    Assertions.assertThat(challenge.answerCorrect(DECRYPTION_ERROR)).isFalse();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge40(resource);
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
