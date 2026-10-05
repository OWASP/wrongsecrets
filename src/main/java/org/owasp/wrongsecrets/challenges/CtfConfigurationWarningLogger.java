package org.owasp.wrongsecrets.challenges;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Warns on startup when CTF mode is enabled while the per-challenge CTF answer of challenge 8, 30,
 * 37 or 67 is still missing, blank or set to its default placeholder.
 *
 * <p>Such a default is not an error: the application keeps running and the challenge simply shows
 * no answer to submit after it has been solved. The warning only tells the operator which
 * configuration entry still holds its default placeholder. Configured values are secrets for the
 * CTF-platform, so they are never logged.
 *
 * <p>When CTF mode is disabled nothing is logged, as these defaults are the expected setup for a
 * normal WrongSecrets instance.
 */
@Slf4j
@Component
public class CtfConfigurationWarningLogger {

  private final CtfAnswerConfiguration ctfAnswerConfiguration;
  private final boolean ctfModeEnabled;

  /**
   * Creates the logger for the per-challenge CTF answer configuration.
   *
   * @param ctfAnswerConfiguration the configuration of challenges 8, 30, 37 and 67
   * @param ctfModeEnabled whether CTF mode is enabled
   */
  public CtfConfigurationWarningLogger(
      CtfAnswerConfiguration ctfAnswerConfiguration,
      @Value("${ctf_enabled}") boolean ctfModeEnabled) {
    this.ctfAnswerConfiguration = ctfAnswerConfiguration;
    this.ctfModeEnabled = ctfModeEnabled;
  }

  /**
   * Logs a warning for every challenge whose CTF answer is still using a default, but only while
   * CTF mode is enabled. Invoked on startup through the {@link ApplicationReadyEvent}.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void warnAboutDefaultCtfAnswers() {
    if (!ctfModeEnabled) {
      return;
    }
    ctfAnswerConfiguration.answers().stream()
        .filter(answer -> !answer.isConfigured())
        .forEach(
            answer ->
                log.warn(
                    "CTF mode is enabled while {} for {} is {}; the default placeholder of this"
                        + " challenge is still active, so no answer is shown when it is solved."
                        + " Set it to the flag you stored in your CTF platform.",
                    answer.propertyName(),
                    answer.challenge(),
                    describeDefaultState(answer)));
  }

  private String describeDefaultState(CtfAnswerConfiguration.CtfAnswer answer) {
    if (answer.value() == null || answer.value().isBlank()) {
      return "missing or blank";
    }
    return "still set to its default placeholder \"" + CtfAnswerConfiguration.NOT_SET + "\"";
  }
}
