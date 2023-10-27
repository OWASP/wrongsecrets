package org.owasp.wrongsecrets.challenges.docker.challenge30;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

public class Challenge30Test {

  @Test
  public void testSpoiler() {
    Challenge30 challenge = new Challenge30();

    Spoiler spoiler = challenge.spoiler();

    assertThat(spoiler).isNotNull();
    assertThat(spoiler.solution()).isNotNull();
    assertThat(spoiler.solution()).hasSize(12);
  }
}
