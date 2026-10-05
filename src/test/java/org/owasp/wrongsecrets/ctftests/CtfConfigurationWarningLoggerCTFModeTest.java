package org.owasp.wrongsecrets.ctftests;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.challenges.CtfAnswerConfiguration;
import org.owasp.wrongsecrets.challenges.CtfConfigurationWarningLogger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Verifies the startup warnings for the per-challenge CTF answers of challenges 8, 30, 37 and 67
 * while CTF mode is enabled: every challenge which still uses a default warns on startup, a
 * configured challenge stays silent, and configured values are never logged.
 */
@SpringBootTest(
    properties = {
      "K8S_ENV=docker",
      "ctf_enabled=true",
      "challenge_sixty_seven_ctf_to_provide_to_host_value=flag-for-challenge-67"
    },
    classes = {
      WrongSecretsApplication.class,
      CtfConfigurationWarningLoggerCTFModeTest.StartupWarningCapture.class
    })
class CtfConfigurationWarningLoggerCTFModeTest {

  private static final CapturingAppender startupAppender = new CapturingAppender();

  @Autowired private CtfAnswerConfiguration ctfAnswerConfiguration;

  @AfterAll
  static void stopCapturingStartupWarnings() {
    var logger = (Logger) LoggerFactory.getLogger(CtfConfigurationWarningLogger.class);
    logger.detachAppender(startupAppender);
    startupAppender.stop();
    startupAppender.messages.clear();
  }

  @Test
  void shouldWarnOnStartupForEveryChallengeThatStillUsesItsDefault() {
    assertThat(startupAppender.messages).hasSize(3);
    assertThat(startupAppender.messages)
        .anyMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_8_PROPERTY));
    assertThat(startupAppender.messages)
        .anyMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_30_PROPERTY));
    assertThat(startupAppender.messages)
        .anyMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_37_PROPERTY));
    assertThat(startupAppender.messages)
        .noneMatch(message -> message.contains(CtfAnswerConfiguration.CHALLENGE_67_PROPERTY));
    assertThat(startupAppender.messages)
        .noneMatch(message -> message.contains("flag-for-challenge-67"));
  }

  @Test
  void shouldReadTheConfigurationEntriesOfEveryChallenge() {
    assertThat(ctfAnswerConfiguration.answers()).hasSize(4);
    assertThat(ctfAnswerConfiguration.challenge8().value()).isEqualTo("not_set");
    assertThat(ctfAnswerConfiguration.challenge30().value()).isEqualTo("not_set");
    assertThat(ctfAnswerConfiguration.challenge37().value()).isEqualTo("not_set");
    assertThat(ctfAnswerConfiguration.challenge67().value()).isEqualTo("flag-for-challenge-67");
    assertThat(ctfAnswerConfiguration.challenge67().isConfigured()).isTrue();
    assertThat(ctfAnswerConfiguration.challenge67().propertyName())
        .isEqualTo("challenge_sixty_seven_ctf_to_provide_to_host_value");
  }

  /**
   * Attaches the capture appender while the application context starts up: Spring Boot (re)loads
   * its logging configuration before any bean exists, so attaching any earlier would be undone.
   */
  @TestConfiguration
  static class StartupWarningCapture {

    @Bean
    InitializingBean attachStartupWarningCapture() {
      return () -> {
        var logger = (Logger) LoggerFactory.getLogger(CtfConfigurationWarningLogger.class);
        startupAppender.setContext(logger.getLoggerContext());
        startupAppender.start();
        logger.addAppender(startupAppender);
      };
    }
  }

  /** Appender which captures the formatted message of every warning of a single logger. */
  private static final class CapturingAppender extends AppenderBase<ILoggingEvent> {

    private final List<String> messages = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
      messages.add(event.getFormattedMessage());
    }
  }
}
