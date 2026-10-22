package org.owasp.wrongsecrets.challenges.cloud;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class Challenge78Test {

  private Logger challengeLogger;
  private CapturingAppender appender;

  @BeforeEach
  void attachAppender() {
    challengeLogger = (Logger) LoggerFactory.getLogger(Challenge78.class);
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
  void rightAnswerShouldSolveChallenge() throws Exception {
    var challenge = new Challenge78(newEd25519PrivateKey());
    var solution = challenge.spoiler().solution();

    assertThat(challenge.answerCorrect(solution)).isTrue();
    assertThat(challenge.spoiler().solution()).isEqualTo(solution);
    assertThat(challenge.answerCorrect("not-the-private-key")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
  }

  @Test
  void pastedKeyWithoutLineBreaksShouldSolveChallenge() throws Exception {
    var challenge = new Challenge78(newEd25519PrivateKey());
    var solution = challenge.spoiler().solution();

    assertThat(challenge.answerCorrect(solution.replace("\n", "").replace("\r", ""))).isTrue();
    assertThat(challenge.answerCorrect(solution.replace("\n", "\r\n"))).isTrue();
  }

  @Test
  void truncatedKeyShouldNotSolveChallenge() throws Exception {
    var challenge = new Challenge78(newEd25519PrivateKey());
    var solution = challenge.spoiler().solution();

    assertThat(challenge.answerCorrect(solution.substring(0, solution.length() / 2))).isFalse();
  }

  @Test
  void unconfiguredChallengeShouldReturnAnAnswerButAcceptNoEmptyInput() {
    var challenge = new Challenge78("");

    assertThat(challenge.spoiler().solution()).isNotBlank();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
  }

  @Test
  void answerShouldNeverBeWrittenToTheLogs() throws Exception {
    var challenge = new Challenge78(newEd25519PrivateKey());
    var solution = challenge.spoiler().solution();

    assertThat(challenge.answerCorrect(solution)).isTrue();
    assertThat(appender.messages).noneMatch(message -> message.contains(solution));
  }

  private static String newEd25519PrivateKey() throws NoSuchAlgorithmException {
    var keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
    var encoded = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    var pem = new StringBuilder("-----BEGIN PRIVATE KEY-----");
    for (var offset = 0; offset < encoded.length(); offset += 64) {
      pem.append('\n').append(encoded, offset, Math.min(offset + 64, encoded.length()));
    }
    return pem.append('\n').append("-----END PRIVATE KEY-----\n").toString();
  }

  private static final class CapturingAppender extends AppenderBase<ILoggingEvent> {

    private final List<String> messages = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent event) {
      messages.add(event.getFormattedMessage());
    }
  }
}
