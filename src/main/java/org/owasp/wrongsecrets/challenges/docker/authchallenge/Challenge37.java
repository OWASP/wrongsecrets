package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import com.google.common.base.Strings;
import java.nio.charset.Charset;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.BasicAuthentication;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on the idea of leaking a secret for an authenticated endpoint through a
 * ZAP configuration file.
 */
@Slf4j
@Component
public class Challenge37 extends FixedAnswerChallenge {

  private String secret;
  private static final String password = "YjNCbGJpQnpaWE5oYldVPQo=";

  public Challenge37(@Value("${DEFAULT37}") String secret) {
    if ("DEFAULT37".equals(secret) || Strings.isNullOrEmpty(secret)) {
      this.secret = UUID.randomUUID().toString();
    } else {
      this.secret = secret;
    }
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
  public String getAnswer() {
    return secret;
  }
}
