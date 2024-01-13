package org.owasp.wrongsecrets.challenges.kubernetes;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.vault.authentication.*;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Primary
public class VaultConfig extends AbstractVaultConfiguration {

  @Value("${spring.cloud.vault.uri}")
  private String vaultAddress;

  @Value("${spring.cloud.vault.role}")
  private String role;

  @Value("${spring.cloud.vault.kubernetes-path}")
  private String tokenPath;

  @Value("${spring.cloud.vault.kubernetes.service-account-token-file}")
  private String tokenFile;

  @Value("${spring.cloud.vault.authentication}")
  private VaultProperties.AuthenticationMethod authenticationMethod;

  @Override
  public @NotNull VaultEndpoint vaultEndpoint() {
    return VaultEndpoint.from(vaultAddress);
  }

  @Override
  public @NotNull ClientAuthentication clientAuthentication() {
    if (VaultProperties.AuthenticationMethod.KUBERNETES.equals(authenticationMethod)) {
      KubernetesJwtSupplier jwtSupplier = new KubernetesServiceAccountTokenFile(tokenFile);
      KubernetesAuthenticationOptions options =
          new KubernetesAuthenticationOptions.KubernetesAuthenticationOptionsBuilder()
              .role(role)
              .path(tokenPath)
              .jwtSupplier(jwtSupplier)
              .build();
      return new KubernetesAuthentication(options, restOperations());
    } else {
      return new TokenAuthentication("empty");
    }
  }
}
