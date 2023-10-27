package org.owasp.wrongsecrets;

import com.google.common.base.Strings;
import java.util.function.Supplier;

public class ConfigurationException extends RuntimeException {
  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(Supplier<String> message) {
    super(message.get());
  }

  public static Supplier<String> configError(
      String errorMessageTemplate, Object... errorMessageArgs) {
    return () -> Strings.lenientFormat(errorMessageTemplate, errorMessageArgs);
  }
}
