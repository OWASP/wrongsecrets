package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.*;
import org.springframework.vault.support.Versioned;

/**
 * This challenge is about having a metadata of secrets stored in a misconfigured Hashicorp Vault.
 */
@Component
@Slf4j
public class MetaDataChallenge extends FixedAnswerChallenge {

  private final String vaultPasswordString;
  private final String vaultUri;
  private final String vaultAuthMethod;

  private VaultEndpoint vaultEndpoint;

  public MetaDataChallenge(
      @Value("${vaultpassword}") String vaultPasswordString,
      @Value("${spring.cloud.vault.uri}") String vaultUri,
      @Value("${spring.cloud.vault.authentication}") String vaultAuthMethod) {
    this.vaultPasswordString = vaultPasswordString;
    this.vaultUri = vaultUri;
    this.vaultAuthMethod = vaultAuthMethod;
  }

  public String getAnswer() {
    try {

      VaultOperations operations = getVaultTemplate();
      VaultVersionedKeyValueOperations versionedOperations =
          operations.opsForVersionedKeyValue("secret");
      Versioned<Map<String, Object>> versioned = versionedOperations.get("wrongsecret");
      if (versioned != null && versioned.getMetadata() != null) {
        String metadata = versioned.getMetadata().getCustomMetadata().get("secret");
        if (Strings.isNullOrEmpty(metadata)) {
          return vaultPasswordString;
        }
        return metadata;
      }
    } catch (Exception e) {
      log.warn("Exception during execution of challenge44", e);
    }
    return vaultPasswordString;
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
}
