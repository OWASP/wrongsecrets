package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on the belief that a static string processed by Pbkdf2 can lead to a
 * seecure setup. <a href="https://www.dcode.fr/pbkdf2-hash">online generator</a>
 */
@Slf4j
@Component
public class Challenge34 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return getKey();
  }

  private String getKey() {
    log.info(
        "The key for challenge34 has been initialized, now you can use it for encryption when"
            + " needed.");
    return generateKey();
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
  }
}
