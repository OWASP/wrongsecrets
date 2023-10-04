package org.owasp.wrongsecrets.challenges.kubernetes;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Class used to get password from vault usisng the springboot cloud integration with vault. */
@ConfigurationProperties("vaultpassword")
public class Vaultpassword {

  private String password;

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasssword() {
    return password;
  }
}
