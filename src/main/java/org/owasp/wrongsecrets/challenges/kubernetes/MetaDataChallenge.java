package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.*;
import org.springframework.vault.support.Versioned;

/**
 * This challenge is about having a metadata of secrets stored in a misconfigured Hashicorp Vault.
 */
@Component
@Slf4j
public class MetaDataChallenge extends FixedAnswerChallenge {

  private final String vaultPasswordString;
  private final VaultTemplate vaultTemplate;

  private final VaultProperties.AuthenticationMethod authenticationMethod;

  public MetaDataChallenge(
      @Value("${vaultpassword}") String vaultPasswordString,
      @Nullable VaultTemplate vaultTemplate,
      @Value("${spring.cloud.vault.authentication}")
          VaultProperties.AuthenticationMethod vaultAuthmethod) {
    this.vaultPasswordString = vaultPasswordString;
    this.vaultTemplate = vaultTemplate;
    this.authenticationMethod = vaultAuthmethod;
  }

  public String getAnswer() {
    try {
      if (VaultProperties.AuthenticationMethod.NONE.equals(authenticationMethod)
          || vaultTemplate == null) {
        log.warn("Vault not setup for challenge 44");
        return vaultPasswordString;
      }
      VaultVersionedKeyValueOperations versionedOperations =
          vaultTemplate.opsForVersionedKeyValue("secret");
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
}
