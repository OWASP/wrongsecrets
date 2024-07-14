package org.owasp.wrongsecrets.challenges.kubernetes;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.Versioned;

@Component
@Slf4j
public class VaultSubKeyChallenge extends FixedAnswerChallenge {

  private final String vaultPasswordString;
  private final VaultTemplate vaultTemplate;

  private final VaultProperties.AuthenticationMethod authenticationMethod;

  public VaultSubKeyChallenge(
      @Value("${vaultpassword}") String vaultPasswordString,
      @Nullable VaultTemplate vaultTemplate,
      @Value("${spring.cloud.vault.authentication}")
          VaultProperties.AuthenticationMethod vaultAuthmethod) {
    this.vaultPasswordString = vaultPasswordString;
    this.vaultTemplate = vaultTemplate;
    this.authenticationMethod = vaultAuthmethod;
  }

  @Override
  public String getAnswer() {
    try {
      if (VaultProperties.AuthenticationMethod.NONE.equals(authenticationMethod)
          || vaultTemplate == null) {
        log.warn("Vault not setup for challenge 45");
        return vaultPasswordString;
      }
      VaultVersionedKeyValueOperations versionedOperations =
          vaultTemplate.opsForVersionedKeyValue("secret");
      Versioned<Map<String, Object>> versioned = versionedOperations.get("wrongsecret");
      if (versioned == null) {
        return vaultPasswordString;
      }
      Optional<String> first = versioned.getRequiredData().keySet().stream().findFirst();
      return first.orElse(vaultPasswordString);

    } catch (Exception e) {
      log.warn("Exception during execution of challenge45", e);
    }
    return vaultPasswordString;
  }
}
