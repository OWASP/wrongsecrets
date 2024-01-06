package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

  private VaultEndpoint vaultEndpoint;

  public MetaDataChallenge(
      @Value("${vaultpassword}") String vaultPasswordString,
      @Value("${spring.cloud.vault.uri}") String vaultUri) {
    this.vaultPasswordString = vaultPasswordString;
    this.vaultUri = vaultUri;
  }

  public String getAnswer() {
    try {
      if (vaultEndpoint == null) {
        vaultEndpoint = initializeVaultEndPoint();
      }
      VaultOperations operations = new VaultTemplate(vaultEndpoint);
      VaultVersionedKeyValueOperations versionedOperations =
          operations.opsForVersionedKeyValue("wrongsecret");
      Versioned<String> versioned = versionedOperations.get("metadatafun", String.class);
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

  private VaultEndpoint initializeVaultEndPoint() {
    if (Strings.isNullOrEmpty(vaultUri)) {
      return new VaultEndpoint();
    }
    return VaultEndpoint.from(vaultUri);
  }
}
