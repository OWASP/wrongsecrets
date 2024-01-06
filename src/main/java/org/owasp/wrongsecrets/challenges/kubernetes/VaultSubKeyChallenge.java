package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.Versioned;

@Component
@Slf4j
public class VaultSubKeyChallenge extends FixedAnswerChallenge {

  private final String vaultPasswordString;
  private final String vaultUri;
  private final String vaultAuthMethod;

  private VaultEndpoint vaultEndpoint;

  public VaultSubKeyChallenge(
      @Value("${vaultpassword}") String vaultPasswordString,
      @Value("${spring.cloud.vault.uri}") String vaultUri,
      @Value("${spring.cloud.vault.authentication}") String vaultAuthMethod) {
    this.vaultPasswordString = vaultPasswordString;
    this.vaultUri = vaultUri;
    this.vaultAuthMethod = vaultAuthMethod;
  }

  /**
   * gets the vault template for the operation: either autowired for kubernetes, or using the token
   * for the unit tests.
   *
   * @return authenticated VaultTemplate
   */
  private VaultTemplate getVaultTemplate() {
    if (vaultEndpoint == null) {
      vaultEndpoint = initializeVaultEndPoint();
    }
    if (Strings.isNullOrEmpty(vaultAuthMethod) || "KUBERNETES".equals(vaultAuthMethod)) {
      return new VaultTemplate(vaultEndpoint);
    }
    return new VaultTemplate(vaultEndpoint, new TokenAuthentication(vaultAuthMethod));
  }

  private VaultEndpoint initializeVaultEndPoint() {
    if (Strings.isNullOrEmpty(vaultUri)) {
      return new VaultEndpoint();
    }
    return VaultEndpoint.from(vaultUri);
  }

  @Override
  public String getAnswer() {
    try {
      VaultOperations operations = getVaultTemplate();
      VaultVersionedKeyValueOperations versionedOperations =
          operations.opsForVersionedKeyValue("secret");
      Versioned<Map<String, Object>> versioned = versionedOperations.get("wrongsecret1");
      if (versioned != null) {
        String s = Objects.requireNonNull(versioned.getData()).keySet().stream().findFirst().get();
        if (Strings.isNullOrEmpty(s)) {
          return vaultPasswordString;
        }
        return s;
      }

      // todo: implement the subkey retrieval here!
    } catch (Exception e) {
      log.warn("Exception during execution of challenge45", e);
    }
    return vaultPasswordString;
  }
}
