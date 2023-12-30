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

  public MetaDataChallenge(@Value("${vaultpassword}") String vaultPasswordString) {
    this.vaultPasswordString = vaultPasswordString;
  }

  public String getAnswer() {
    try {
      VaultOperations operations = new VaultTemplate(new VaultEndpoint());
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
}
