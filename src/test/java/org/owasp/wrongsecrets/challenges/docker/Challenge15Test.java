package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge15Test {

  @Test
  void solveChallenge15() {
    Challenge15 challenge15 =
        new Challenge15(
            "qemGhPXJjmipa9O7cYBJnuO79BQg/MgvSFbV9rhiBFuEmVqEfDsuz6xfBDMV2lH8TAhwKX39OrW+WIYxgaEWl8c1/n93Yxz5G/ZKbuTBbEaJ58YvC88IoB4NtnQciU6p+uJ+P+uHMMzRGQ0oGNvQeb5+bKK9V62Rp4aOhDupHnjeTUPKmWUV9/lzC5IUM7maNGuBLllzJnoM6QHMnGe5YpBBEA==");
    assertThat(challenge15.spoiler().toString()).contains("aws");
    assertThat(challenge15.answerCorrect(challenge15.spoiler().solution())).isTrue();
  }
}
