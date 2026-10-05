package org.owasp.wrongsecrets.challenges;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class CtfConfigurationWarningLoggerTest {

  private static final String DEFAULT = CtfAnswerConfiguration.NOT_SET;
  private static final String CONFIGURED = "flag-stored-in-the-ctf-platform";

  private Logger warningLogger;
  private CapturingAppender appender;

  @BeforeEach
  void attachAppender() {
    warningLogger = (Logger) LoggerFactory.getLogger(CtfConfigurationWarningLogger.class);
    appender = new CapturingAppender();
    appender.setContext(warningLogger.getLoggerContext());
    appender.start();
    warningLogger.addAppender(appender);
  }

  @AfterEach
  void detachAppender() {
    warningLogger.detachAppender(appender);
    appender.stop();
  }

  @Test
  void shouldWarnForEveryChallengeThatStillUsesItsDefaultWhileCtfModeIsEnabled() {
    loggerWithDefaults(true).warnAboutDefaultCtfAnswers();

    assertThat(appender.messages).hasSize(4);
    assertThat(appender.levels).containsOnly(Level.WARN);
    for (String propertyName : allConfiguredProperties()) {
      assertThat(appender.messages).anyMatch(message -> message.contains(propertyName));
    }
    assertThat(appender.messages)
        .allSatisfy(
            message ->
                assertThat(message)
                    .contains("the default placeholder of this challenge is still active"));
  }

  @Test
  void shouldWarnOnlyForTheChallengesWithoutAConfiguration() {
    new CtfConfigurationWarningLogger(configuration(CONFIGURED, "", DEFAULT, CONFIGURED), true)
        .warnAboutDefaultCtfAnswers();

    assertThat(appender.messages).hasSize(2);
    assertThat(appender.messages)
        .anyMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_30_PROPERTY));
    assertThat(appender.messages)
        .anyMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_37_PROPERTY));
    assertThat(appender.messages)
        .noneMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_8_PROPERTY));
    assertThat(appender.messages)
        .noneMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_67_PROPERTY));
  }

  @Test
  void shouldDistinguishMissingOrBlankConfigurationFromTheDefaultPlaceholder() {
    new CtfConfigurationWarningLogger(configuration("", DEFAULT, CONFIGURED, CONFIGURED), true)
        .warnAboutDefaultCtfAnswers();

    assertThat(appender.messages).hasSize(2);
    assertThat(appender.messages)
        .anySatisfy(
            message ->
                assertThat(message)
                    .contains(CtfAnswerConfiguration.CHALLENGE_8_PROPERTY)
                    .contains("missing or blank"));
    assertThat(appender.messages)
        .anySatisfy(
            message ->
                assertThat(message)
                    .contains(CtfAnswerConfiguration.CHALLENGE_30_PROPERTY)
                    .contains(DEFAULT));
  }

  @Test
  void shouldNotWarnWhileCtfModeIsDisabled() {
    loggerWithDefaults(false).warnAboutDefaultCtfAnswers();

    assertThat(appender.messages).isEmpty();
    assertThat(appender.levels).isEmpty();
  }

  @Test
  void shouldNotWarnWhenEveryChallengeHasItsOwnConfiguration() {
    new CtfConfigurationWarningLogger(
            configuration(CONFIGURED, CONFIGURED, CONFIGURED, CONFIGURED), true)
        .warnAboutDefaultCtfAnswers();

    assertThat(appender.messages).isEmpty();
  }

  @Test
  void shouldNeverLogConfiguredValues() {
    new CtfConfigurationWarningLogger(
            configuration(CONFIGURED, DEFAULT, CONFIGURED, CONFIGURED), true)
        .warnAboutDefaultCtfAnswers();

    assertThat(appender.messages).hasSize(1);
    assertThat(appender.messages).noneMatch(message -> message.contains(CONFIGURED));
  }

  private CtfConfigurationWarningLogger loggerWithDefaults(boolean ctfModeEnabled) {
    return new CtfConfigurationWarningLogger(
        configuration(DEFAULT, DEFAULT, DEFAULT, DEFAULT), ctfModeEnabled);
  }

  private CtfAnswerConfiguration configuration(
      String challenge8Value,
      String challenge30Value,
      String challenge37Value,
      String challenge67Value) {
    return new CtfAnswerConfiguration(
        challenge8Value, challenge30Value, challenge37Value, challenge67Value);
  }

  private List<String> allConfiguredProperties() {
    return List.of(
        CtfAnswerConfiguration.CHALLENGE_8_PROPERTY,
        CtfAnswerConfiguration.CHALLENGE_30_PROPERTY,
        CtfAnswerConfiguration.CHALLENGE_37_PROPERTY,
        CtfAnswerConfiguration.CHALLENGE_67_PROPERTY);
  }

  /** Appender which captures the formatted message and level of every warning. */
  private static final class CapturingAppender extends AppenderBase<ILoggingEvent> {

    private final List<String> messages = new ArrayList<>();
    private final List<Level> levels = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
      messages.add(event.getFormattedMessage());
      levels.add(event.getLevel());
    }
  }
}
