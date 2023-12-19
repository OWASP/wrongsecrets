package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
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
    var putResult =
        vaultContainer.execInContainer(
            "vault",
            "kv",
            "metadata",
            "put",
            "-mount=secret",
            "-custom-metadata=foo=bar",
            "my-secret");

    assertThat(putResult.getStdout())
        .contains("Success! Data written to: secret/metadata/my-secret");

    ExecResult readResult =
        vaultContainer.execInContainer(
            "vault", "kv", "metadata", "get", "-mount=secret", "my-secret");
    assertThat(readResult.getStdout()).contains("foo:bar");
  }
}
