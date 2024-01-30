package org.owasp.wrongsecrets.challenges.kubernetes;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Class used to get value from vault using the springboot cloud integration with vault. */
@ConfigurationProperties("vaultinjected")
public class Vaultinjected {

  private String value;

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
