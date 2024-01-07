package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.VaultProperties;
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
  private final VaultProperties.AuthenticationMethod vaultAuthMethod;
  private final String authToken;

  private VaultEndpoint vaultEndpoint;

  public MetaDataChallenge(
      @Value("${vaultpassword}") String vaultPasswordString,
      @Value("${spring.cloud.vault.uri}") String vaultUri,
      @Value("${spring.cloud.vault.authentication}")
          VaultProperties.AuthenticationMethod vaultAuthMethod,
      @Value("${vaulttoken") final String authToken) {
    this.vaultPasswordString = vaultPasswordString;
    this.vaultUri = vaultUri;
    this.vaultAuthMethod = vaultAuthMethod;
    this.authToken = authToken;
  }

  public String getAnswer() {
    try {
      if (vaultAuthMethod == null
          || vaultAuthMethod.equals(VaultProperties.AuthenticationMethod.NONE)) {
        log.warn("Vault not setup for challenge 44");
        return vaultPasswordString;
      }
      VaultOperations operations = getVaultTemplate();
      VaultVersionedKeyValueOperations versionedOperations =
          operations.opsForVersionedKeyValue("secret");
      Versioned<Map<String, Object>> versioned = versionedOperations.get("wrongsecret");
      if (versioned == null) {
        return vaultPasswordString;
      }
      var metadata = versioned.getMetadata();
      if (metadata == null) {
        return vaultPasswordString;
      }
      var customMetadata = metadata.getCustomMetadata();
      if (!customMetadata.isEmpty()) {
        String customMedataSecret = customMetadata.get("secret");
        if (Strings.isNullOrEmpty(customMedataSecret)) {
          return vaultPasswordString;
        }
        return customMedataSecret;
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
    if (Strings.isNullOrEmpty(vaultAuthMethod.toString())
        || VaultProperties.AuthenticationMethod.KUBERNETES.equals(vaultAuthMethod)) {
      return new VaultTemplate(vaultEndpoint);
    }
    // assume VaultProperties.AuthenticationMethod.TOKEN
    return new VaultTemplate(vaultEndpoint, new TokenAuthentication(authToken));
  }

  private VaultEndpoint initializeVaultEndPoint() {
    if (Strings.isNullOrEmpty(vaultUri)) {
      return new VaultEndpoint();
    }
    return VaultEndpoint.from(vaultUri);
  }
}
