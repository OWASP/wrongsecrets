package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.vault.VaultContainer;

@Testcontainers
public class Challenge44Test {
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
            "vaultpassword.password='$(openssl rand -base64 16)'");
    assertThat(putSecretResult.getStdout()).contains("secret/data/wrongsecret");

    var putResult =
        vaultContainer.execInContainer(
            "vault",
            "kv",
            "metadata",
            "put",
            "-mount=secret",
            "-custom-metadata=secret=test",
            "wrongsecret");

    assertThat(putResult.getStdout())
        .contains("Success! Data written to: secret/metadata/wrongsecret");

    ExecResult readResult =
        vaultContainer.execInContainer(
            "vault", "kv", "metadata", "get", "-mount=secret", "wrongsecret");
    assertThat(readResult.getStdout()).contains("map[secret:test]");
    String address = vaultContainer.getHttpHostAddress();
    assertThat(readResult.getStdout()).contains("test");

    var metadataChallenge =
        new MetaDataChallenge(
            "ACTUAL_ANSWER_CHALLENGE7",
            new VaultTemplate(VaultEndpoint.from(address), new TokenAuthentication(VAULT_TOKEN)),
            VaultProperties.AuthenticationMethod.TOKEN);
    assertThat(metadataChallenge.spoiler().solution()).isEqualTo("test");
  }
}
