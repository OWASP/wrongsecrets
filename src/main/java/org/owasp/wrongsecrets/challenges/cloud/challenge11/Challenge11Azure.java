package org.owasp.wrongsecrets.challenges.cloud.challenge11;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Cloud challenge which uses IAM privilelge escalation (differentiating per cloud). */
@Component
@Slf4j
public class Challenge11Azure implements Challenge {

  private final String azureDefaultValue;
  private final Supplier<String> challengeAnswer;
  private final String azureVaultUri;
  private final String azureWrongSecret3;
  private final String ctfValue;
  private final boolean ctfEnabled;

  public Challenge11Azure(
      @Value("${default_azure_value}") String azureDefaultValue,
      @Value("${spring.cloud.azure.keyvault.secret.property-sources[0].endpoint}")
          String azureVaultUri,
      @Value("${wrongsecret-3}") String azureWrongSecret3, // Exclusively auto-wired for Azure
      @Value("${default_aws_value_challenge_11}") String ctfValue,
      @Value("${ctf_enabled}") boolean ctfEnabled,
      RuntimeEnvironment runtimeEnvironment) {
    this.azureDefaultValue = azureDefaultValue;
    this.azureVaultUri = azureVaultUri;
    this.azureWrongSecret3 = azureWrongSecret3;
    this.ctfValue = ctfValue;
    this.ctfEnabled = ctfEnabled;
    this.challengeAnswer = Suppliers.memoize(getChallenge11Value(runtimeEnvironment));
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(challengeAnswer.get());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return challengeAnswer.get().equals(answer);
  }

  private Supplier<String> getChallenge11Value(RuntimeEnvironment runtimeEnvironment) {
    if (!ctfEnabled) {
      return () -> getAzureChallenge11Value();
    } else if (!Strings.isNullOrEmpty(ctfValue)
        && !Strings.isNullOrEmpty(azureDefaultValue)
        && !ctfValue.equals(azureDefaultValue)) {
      return () -> ctfValue;
    }

    log.info("CTF enabled, skipping challenge11");
    return () -> "please_use_supported_cloud_env";
  }

  private String getAzureChallenge11Value() {
    log.info(String.format("Using Azure Key Vault URI: %s", azureVaultUri));
    return azureWrongSecret3;
  }
}
