package org.owasp.wrongsecrets.challenges;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Single source of truth for the per-challenge CTF answers of challenges 8, 30, 37 and 67.
 *
 * <p>These challenges generate a random answer which the CTF-scoring-environment cannot know, so
 * every one of them has its own, independent configuration entry holding the flag that needs to be
 * submitted in the CTF-platform. When a player solves such a challenge in CTF mode (with a {@code
 * CTF_SERVER_ADDRESS} configured), the value configured for that specific challenge is shown as the
 * answer to submit.
 *
 * <p>Until an entry is overridden it keeps its default placeholder {@link #NOT_SET}. A default is
 * never an error: the challenge simply does not show an answer to submit, and {@link
 * CtfConfigurationWarningLogger} warns about it on startup while CTF mode is enabled.
 *
 * <p>See <a
 * href="https://github.com/OWASP/wrongsecrets/blob/master/ctf-instructions.md">ctf-instructions.md</a>
 * for the exact configuration names.
 */
@Component
public class CtfAnswerConfiguration {

  /** Default placeholder which is active until a real CTF answer has been configured. */
  public static final String NOT_SET = "not_set";

  /** Configuration of the CTF answer shown when challenge 8 is solved. */
  public static final String CHALLENGE_8_PROPERTY = "challenge_acht_ctf_to_provide_to_host_value";

  /** Configuration of the CTF answer shown when challenge 30 is solved. */
  public static final String CHALLENGE_30_PROPERTY =
      "challenge_thirty_ctf_to_provide_to_host_value";

  /** Configuration of the CTF answer shown when challenge 37 is solved. */
  public static final String CHALLENGE_37_PROPERTY =
      "challenge_rando_key_ctf_to_provide_to_host_value";

  /** Configuration of the CTF answer shown when challenge 67 is solved. */
  public static final String CHALLENGE_67_PROPERTY =
      "challenge_sixty_seven_ctf_to_provide_to_host_value";

  /**
   * The CTF answer configured for a single challenge.
   *
   * @param challenge the challenge the answer belongs to, used for logging
   * @param propertyName the configuration property holding the answer
   * @param value the configured value, which can be missing, blank or {@link #NOT_SET}
   */
  public record CtfAnswer(String challenge, String propertyName, String value) {

    /**
     * Tells whether an actual CTF answer has been configured instead of a default.
     *
     * @return true when a value is present and no longer the default placeholder
     */
    public boolean isConfigured() {
      return value != null && !value.isBlank() && !NOT_SET.equals(value);
    }
  }

  private final CtfAnswer challenge8;
  private final CtfAnswer challenge30;
  private final CtfAnswer challenge37;
  private final CtfAnswer challenge67;

  /**
   * Reads the CTF answer configuration of challenges 8, 30, 37 and 67. Each value falls back to an
   * empty value when the configuration entry is missing, so a missing entry can be warned about on
   * startup instead of breaking the application.
   *
   * @param challenge8Value the configuration of challenge 8
   * @param challenge30Value the configuration of challenge 30
   * @param challenge37Value the configuration of challenge 37
   * @param challenge67Value the configuration of challenge 67
   */
  public CtfAnswerConfiguration(
      @Value("${" + CHALLENGE_8_PROPERTY + ":}") String challenge8Value,
      @Value("${" + CHALLENGE_30_PROPERTY + ":}") String challenge30Value,
      @Value("${" + CHALLENGE_37_PROPERTY + ":}") String challenge37Value,
      @Value("${" + CHALLENGE_67_PROPERTY + ":}") String challenge67Value) {
    this.challenge8 = new CtfAnswer("challenge 8", CHALLENGE_8_PROPERTY, challenge8Value);
    this.challenge30 = new CtfAnswer("challenge 30", CHALLENGE_30_PROPERTY, challenge30Value);
    this.challenge37 = new CtfAnswer("challenge 37", CHALLENGE_37_PROPERTY, challenge37Value);
    this.challenge67 = new CtfAnswer("challenge 67", CHALLENGE_67_PROPERTY, challenge67Value);
  }

  /**
   * The configured CTF answer of challenge 8.
   *
   * @return the configuration of challenge 8
   */
  public CtfAnswer challenge8() {
    return challenge8;
  }

  /**
   * The configured CTF answer of challenge 30.
   *
   * @return the configuration of challenge 30
   */
  public CtfAnswer challenge30() {
    return challenge30;
  }

  /**
   * The configured CTF answer of challenge 37.
   *
   * @return the configuration of challenge 37
   */
  public CtfAnswer challenge37() {
    return challenge37;
  }

  /**
   * The configured CTF answer of challenge 67.
   *
   * @return the configuration of challenge 67
   */
  public CtfAnswer challenge67() {
    return challenge67;
  }

  /**
   * All per-challenge CTF answer configurations, in challenge order.
   *
   * @return the configuration of challenges 8, 30, 37 and 67
   */
  public List<CtfAnswer> answers() {
    return List.of(challenge8, challenge30, challenge37, challenge67);
  }
}
