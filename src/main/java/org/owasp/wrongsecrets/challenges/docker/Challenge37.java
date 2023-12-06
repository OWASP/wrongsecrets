package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.BasicAuthentication;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on the idea of leaking a secret for an authenticated endpoint through a
 * ZAP configuration file.
 */
@Slf4j
@Component
public class Challenge37 implements Challenge {

  private String secret;
  private static final String password = "YjNCbGJpQnpaWE5oYldVPQo=";

  public Challenge37() {
    secret = UUID.randomUUID().toString();
  }

  @Bean
  public BasicAuthentication challenge37BasicAuth() {
    return new BasicAuthentication(
        "Aladdin",
        new String(
            Base64.decode(new String(Base64.decode(password), Charset.defaultCharset())),
            Charset.defaultCharset()),
        "ADMIN",
        "authenticated/**");
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(secret);
  }

  @Override
  public boolean answerCorrect(String answer) {
    return secret.equals(answer);
  }

  public String getPassword() {

    return new String(Base64.decode(Base64.decode(Base64.decode(password))), StandardCharsets.UTF_8)
        .replaceAll("\\r|\\n", "");
  }
}
