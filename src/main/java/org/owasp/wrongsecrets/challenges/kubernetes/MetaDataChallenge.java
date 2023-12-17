package org.owasp.wrongsecrets.challenges.kubernetes;

import java.util.Objects;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.*;
import org.springframework.vault.support.Versioned;

public class MetaDataChallenge extends FixedAnswerChallenge {

  public String getAnswer() {
    VaultOperations operations = new VaultTemplate(new VaultEndpoint());
    VaultVersionedKeyValueOperations versionedOperations =
        operations.opsForVersionedKeyValue("wrongsecret");
    Versioned<String> versioned = versionedOperations.get("metadatafun", String.class);
    assert versioned != null;
    return Objects.requireNonNull(versioned.getMetadata()).getCustomMetadata().get("secret");
  }
}
