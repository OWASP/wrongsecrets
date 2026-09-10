package org.owasp.wrongsecrets.challenges.cloud;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class Challenge67Test {

  private static final String CONFIGURED_SECRET = "cloudwatch-leak-42";

  private Logger challengeLogger;
  private CapturingAppender appender;

  @BeforeEach
  void attachAppender() {
    challengeLogger = (Logger) LoggerFactory.getLogger(Challenge67.class);
    appender = new CapturingAppender();
    appender.setContext(challengeLogger.getLoggerContext());
    appender.start();
    challengeLogger.addAppender(appender);
  }

  @AfterEach
  void detachAppender() {
    challengeLogger.detachAppender(appender);
    appender.stop();
  }

  @Test
  void spoilerShouldRevealConfiguredSecretAndSolveAnswer() {
    var challenge = new Challenge67(CONFIGURED_SECRET);

    assertThat(challenge.spoiler().solution()).isEqualTo(CONFIGURED_SECRET);
    assertThat(challenge.answerCorrect(CONFIGURED_SECRET)).isTrue();
  }

  @Test
  void spoilerShouldRevealGeneratedSecretWhenNotConfigured() {
    var challenge = new Challenge67("not_set");

    var answer = challenge.spoiler().solution();

    assertThat(answer).isNotEmpty().hasSize(16).doesNotContain("not_set");
    assertThat(challenge.answerCorrect(answer)).isTrue();
  }

  @Test
  void spoilerShouldRevealGeneratedSecretWhenConfiguredValueIsBlank() {
    var challenge = new Challenge67("   ");

    var answer = challenge.spoiler().solution();

    assertThat(answer).hasSize(16);
    assertThat(challenge.answerCorrect(answer)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge67(CONFIGURED_SECRET);

    assertThat(challenge.answerCorrect("not-the-secret")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void answerShouldBeLoggedBase64EncodedAndNeverInPlainText() {
    var challenge = new Challenge67(CONFIGURED_SECRET);

    var answer = challenge.spoiler().solution();
    var expectedEncoded =
        Base64.getEncoder().encodeToString(answer.getBytes(StandardCharsets.UTF_8));

    assertThat(appender.messages).isNotEmpty();
    assertThat(appender.messages).anyMatch(message -> message.contains(expectedEncoded));
    assertThat(appender.messages).noneMatch(message -> message.contains(answer));
    assertThat(appender.levels).contains(Level.INFO);
  }

  @Test
  void encodedAnswerShouldBeAttachedAsStructuredLogField() {
    var challenge = new Challenge67(CONFIGURED_SECRET);

    var answer = challenge.spoiler().solution();
    var expectedEncoded =
        Base64.getEncoder().encodeToString(answer.getBytes(StandardCharsets.UTF_8));

    assertThat(appender.mdcSnapshots)
        .anySatisfy(
            mdc -> {
              assertThat(mdc).containsEntry("wrongsecrets.challenge", "challenge-67");
              assertThat(mdc).containsEntry("audit.payload", expectedEncoded);
            });
  }

  @Test
  void answerShouldBeCachedSoTheSecretIsOnlyLeakedOnce() {
    var challenge = new Challenge67("not_set");

    var first = challenge.spoiler().solution();
    var second = challenge.spoiler().solution();

    assertThat(first).isEqualTo(second);
    assertThat(appender.messages).hasSize(1);
  }

  @Test
  void mdcShouldBeCleanedUpAfterLogging() {
    new Challenge67(CONFIGURED_SECRET).spoiler();

    assertThat(org.slf4j.MDC.get("audit.payload")).isNull();
    assertThat(org.slf4j.MDC.get("wrongsecrets.challenge")).isNull();
  }

  /**
   * Appender which snapshots the message, level and MDC while the event is being appended. Logback
   * populates {@link ILoggingEvent#getMDCPropertyMap()} lazily, so reading it after the challenge
   * cleared the MDC would return an empty map.
   */
  private static final class CapturingAppender extends AppenderBase<ILoggingEvent> {

    private final List<String> messages = new ArrayList<>();
    private final List<Level> levels = new ArrayList<>();
    private final List<Map<String, String>> mdcSnapshots = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
      messages.add(event.getFormattedMessage());
      levels.add(event.getLevel());
      mdcSnapshots.add(new HashMap<>(event.getMDCPropertyMap()));
    }
  }
}
