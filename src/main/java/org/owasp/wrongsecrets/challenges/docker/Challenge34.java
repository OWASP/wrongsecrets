package org.owasp.wrongsecrets.challenges.docker;

<<<<<<< HEAD
import com.google.common.base.Strings;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
=======
import java.util.List;
>>>>>>> cefa8809 ( Feature(#614): Added test files)
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
<<<<<<< HEAD
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on the belief that a static string processed by Pbkdf2 can lead to a
 * seecure setup. <a href="https://www.dcode.fr/pbkdf2-hash">online generator</a>
 */
@Slf4j
=======
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on leaking secrets with the misuse of Git notes
 */
>>>>>>> cefa8809 ( Feature(#614): Added test files)
@Component
@Order(34)
public class Challenge34 extends Challenge {

<<<<<<< HEAD
  private String key;

=======
>>>>>>> cefa8809 ( Feature(#614): Added test files)
  public Challenge34(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
<<<<<<< HEAD
    return new Spoiler(getKey());
=======
    return new Spoiler(getSolution());
>>>>>>> cefa8809 ( Feature(#614): Added test files)
  }

  @Override
  public boolean answerCorrect(String answer) {
<<<<<<< HEAD
    return getKey().equals(answer);
=======
    return getSolution().equals(answer);
>>>>>>> cefa8809 ( Feature(#614): Added test files)
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
<<<<<<< HEAD
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} This is a crypto (hashing) type of challenge */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.CRYPTOGRAPHY.id;
=======
    return Difficulty.EASY;
  }

  /** {@inheritDoc} Git based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.GIT.id;
>>>>>>> cefa8809 ( Feature(#614): Added test files)
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

<<<<<<< HEAD
  private String getKey() {
    if (Strings.isNullOrEmpty(key)) {
      key = generateKey();
    }
    log.info(
        "The key for challenge34 has been initialized, now you can use it for encryption when"
            + " needed.");
    return key;
  }

  private String generateKey() {
    String encryptedKey = "123%78___+WEssweLKWEJROUVHLAMW,NERO";
    // note the static salt in use to get to the same key. otherwise the key is not reusable.
    Pbkdf2PasswordEncoder encoder =
        new Pbkdf2PasswordEncoder(
            "secret_salt",
            0,
            100000,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    encoder.setEncodeHashAsBase64(true);

    return encoder.encode(encryptedKey);
=======
  private String getSolution() {
    return unobfuscate("UOZFGZTLOLLXHTKEGGS");
  }

  private String unobfuscate(String obfuscatedString) {
    final String key = "QWERTYUIOPASDFGHJKLZXCVBNM";
    StringBuilder plainText = new StringBuilder();
    for (char c : obfuscatedString.toCharArray()) {
      if (Character.isLetter(c)) {
        int index = key.indexOf(Character.toUpperCase(c));
        char replacement = (char) ('A' + index);
        plainText.append(replacement);
      } else {
        plainText.append(c);
        System.out.println(plainText);
      }
    }
    return plainText.toString();
>>>>>>> cefa8809 ( Feature(#614): Added test files)
  }
}
