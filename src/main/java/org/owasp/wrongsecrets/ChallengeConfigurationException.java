package org.owasp.wrongsecrets;

import com.google.common.base.Strings;
import java.util.function.Supplier;

public class ChallengeConfigurationException extends RuntimeException {
  public ChallengeConfigurationException(String message) {
    super(message);
  }

  public ChallengeConfigurationException(Supplier<String> message) {
    super(message.get());
  }

  public static Supplier<String> configError(
      String errorMessageTemplate, Object... errorMessageArgs) {
    return () -> Strings.lenientFormat(errorMessageTemplate, errorMessageArgs);
  }
}
