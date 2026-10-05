package org.owasp.wrongsecrets.ctftests;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
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
 * Verifies that the startup warnings for the per-challenge CTF answers stay silent while CTF mode
 * is disabled: the defaults of challenges 8, 30, 37 and 67 are the expected setup for a normal
 * WrongSecrets instance.
 */
@SpringBootTest(
    properties = {"K8S_ENV=docker", "ctf_enabled=false"},
    classes = {
      WrongSecretsApplication.class,
      CtfConfigurationWarningLoggerWithoutCtfModeTest.StartupWarningCapture.class
    })
class CtfConfigurationWarningLoggerWithoutCtfModeTest {

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
  void shouldNotWarnOnStartupWhileCtfModeIsDisabled() {
    var attachedAppenders = new ArrayList<Appender<ILoggingEvent>>();
    var logger = (Logger) LoggerFactory.getLogger(CtfConfigurationWarningLogger.class);
    logger.iteratorForAppenders().forEachRemaining(attachedAppenders::add);

    assertThat(attachedAppenders).contains(startupAppender);
    assertThat(startupAppender.messages).isEmpty();
  }

  @Test
  void shouldStillReadTheDefaultConfigurationOfEveryChallenge() {
    assertThat(ctfAnswerConfiguration.answers())
        .allSatisfy(answer -> assertThat(answer.isConfigured()).isFalse());
    assertThat(ctfAnswerConfiguration.challenge67().value()).isEqualTo("not_set");
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
