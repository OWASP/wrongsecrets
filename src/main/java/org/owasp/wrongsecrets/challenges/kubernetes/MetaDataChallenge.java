package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.*;

public class MetaDataChallenge implements Challenge {

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getAnswer());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer) && answer.equals(getAnswer());
  }

  private String getAnswer() {
    VaultOperations operations = new VaultTemplate(new VaultEndpoint());
    VaultKeyValueOperations keyValueOperations =
        operations.opsForKeyValue(
            "wrongSecret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
    // todo conitnue with
    // https://docs.spring.io/spring-vault/reference/vault/vault-secret-engines.html#vault.core.backends.kv2 and the example for the rest!
    return "";
  }
}
