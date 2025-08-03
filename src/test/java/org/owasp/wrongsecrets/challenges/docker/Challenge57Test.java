package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge57Test {

  @Test
  void spoilerShouldReturnCorrectAnswer() {
    var challenge = new Challenge57();
    assertThat(challenge.spoiler())
        .isEqualTo(
            new Spoiler("sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA"));
  }

  @Test
  void answerCorrectShouldReturnTrueForCorrectAnswer() {
    var challenge = new Challenge57();
    assertThat(
            challenge.answerCorrect(
                "sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA"))
        .isTrue();
  }

  @Test
  void answerCorrectShouldReturnFalseForIncorrectAnswer() {
    var challenge = new Challenge57();
    assertThat(challenge.answerCorrect("wronganswer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
  }

  @Test
  void answerCorrectShouldTrimWhitespace() {
    var challenge = new Challenge57();
    assertThat(
            challenge.answerCorrect(
                "  sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA  "))
        .isTrue();
  }

  @Test
  void getLLMJavaScriptCodeShouldExposeAPIKey() {
    var challenge = new Challenge57();
    String jsCode = challenge.getLLMJavaScriptCode();

    // Verify that the JavaScript code contains the exposed API key
    assertThat(jsCode)
        .contains("sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA");
    assertThat(jsCode).contains("this.apiKey = ");
    assertThat(jsCode).contains("console.log");
    assertThat(jsCode).contains("Authorization");
    assertThat(jsCode).contains("Bearer");
  }
}
