package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.vault.VaultContainer;

@Testcontainers
public class Challenge45Test {
  private static final String VAULT_TOKEN = "my-token";

  @Container
  public static VaultContainer<?> vaultContainer =
      new VaultContainer<>("hashicorp/vault:1.13")
          .withVaultToken(VAULT_TOKEN)
          .withInitCommand("secrets enable transit");

  @Test
  public void readFirstSecretPathWithCli() throws Exception {
    var putSecretResult =
        vaultContainer.execInContainer(
            "vault",
            "kv",
            "put",
            "secret/wrongsecret",
            "aaasecret.password='$(openssl rand -base64 16)'");
    assertThat(putSecretResult.getStdout()).contains("secret/data/wrongsecret");
    String address = vaultContainer.getHttpHostAddress();
    var subkeyChallenge =
        new VaultSubKeyChallenge(
            "ACTUAL_ANSWER_CHALLENGE7",
            new VaultTemplate(VaultEndpoint.from(address), new TokenAuthentication(VAULT_TOKEN)),
            VaultProperties.AuthenticationMethod.TOKEN);
    assertThat(subkeyChallenge.spoiler().solution()).isEqualTo("aaasecret.password");
  }
}
