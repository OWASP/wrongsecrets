package org.owasp.wrongsecrets.challenges.cloud.challenge11;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Cloud challenge which uses IAM privilelge escalation (differentiating per cloud). */
@Component
@Slf4j
public class Challenge11Azure extends FixedAnswerChallenge {

  private final String azureDefaultValue;
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
      @Value("${ctf_enabled}") boolean ctfEnabled) {

    this.azureDefaultValue = azureDefaultValue;
    this.azureVaultUri = azureVaultUri;
    this.azureWrongSecret3 = azureWrongSecret3;
    this.ctfValue = ctfValue;
    this.ctfEnabled = ctfEnabled;
  }

  private String getChallenge11Value() {
    if (!ctfEnabled) {
      return getAzureChallenge11Value();
    } else if (!Strings.isNullOrEmpty(ctfValue)
        && !Strings.isNullOrEmpty(azureDefaultValue)
        && !ctfValue.equals(azureDefaultValue)) {
      return ctfValue;
    }

    log.info("CTF enabled, skipping challenge11");
    return "please_use_supported_cloud_env";
  }

  private String getAzureChallenge11Value() {
    log.info(String.format("Using Azure Key Vault URI: %s", azureVaultUri));
    return azureWrongSecret3;
  }

  @Override
  public String getAnswer() {
    return getChallenge11Value();
  }
}
