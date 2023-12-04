package org.owasp.wrongsecrets;

import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.owasp.wrongsecrets.definitions.Environment;
import org.springframework.beans.factory.annotation.Value;

/**
 * Class establishing whether a challenge can run or not depending on the given RuntimeEnvironment
 * and whether components are configured and the CTFmode is enabled or not.
 */
@Slf4j
@ToString
public class RuntimeEnvironment {

  @Value("${ctf_enabled}")
  private boolean ctfModeEnabled;

  @Value("${SPECIAL_K8S_SECRET}")
  private String challenge5Value; // used to determine if k8s/vault challenges are overridden;

  @Value("${vaultpassword}")
  private String challenge7Value;

  @Value("${default_aws_value_challenge_9}")
  private String
      defaultChallenge9Value; // used to determine if the cloud challenge values are overridden

  @Getter private final Environment runtimeEnvironment;

  public RuntimeEnvironment(Environment runtimeEnvironment) {
    this.runtimeEnvironment = runtimeEnvironment;
  }

  public boolean canRun(ChallengeDefinition challengeDefinition) {
    if (isCloudUnlockedInCTFMode()) {
      return true;
    }
    if (isVaultUnlockedInCTFMode() && isK8sUnlockedInCTFMode()) {
      return challengeDefinition.supportedEnvironments().contains(runtimeEnvironment);
    }
    if (isK8sUnlockedInCTFMode()) {
      return challengeDefinition.supportedEnvironments().contains(runtimeEnvironment);
    }
    return challengeDefinition.supportedEnvironments().contains(this.runtimeEnvironment);
  }

  public boolean runtimeInCTFMode() {
    return ctfModeEnabled;
  }

  private boolean isK8sUnlockedInCTFMode() {
    String defaultValueChallenge5 = "if_you_see_this_please_use_k8s";
    return ctfModeEnabled && !challenge5Value.equals(defaultValueChallenge5);
  }

  private boolean isVaultUnlockedInCTFMode() {
    String defaultVaultAnswer = "ACTUAL_ANSWER_CHALLENGE7";
    String secondDefaultVaultAnswer = "if_you_see_this_please_use_K8S_and_Vault";
    return ctfModeEnabled
        && !challenge7Value.equals(defaultVaultAnswer)
        && !challenge7Value.equals(secondDefaultVaultAnswer);
  }

  private boolean isCloudUnlockedInCTFMode() {
    String defaultValueAWSValue = "if_you_see_this_please_use_AWS_Setup";
    return ctfModeEnabled && !defaultChallenge9Value.equals(defaultValueAWSValue);
  }

  public static RuntimeEnvironment fromString(
      String currentRuntimeEnvironment, ChallengeDefinitionsConfiguration challengeDefinitions) {
    var runtimeEnvironment =
        challengeDefinitions.environments().stream()
            .filter(env -> env.name().equalsIgnoreCase(currentRuntimeEnvironment))
            .findFirst()
            .orElseThrow(
                () -> {
                  log.error(
                      "Unable to determine the runtime environment. Make sure K8S_ENV contains one"
                          + " of the expected values: {}.",
                      challengeDefinitions.environments().stream()
                          .map(Environment::name)
                          .collect(Collectors.joining()));
                  throw new MissingEnvironmentException(
                      currentRuntimeEnvironment, challengeDefinitions.environments());
                });
    return new RuntimeEnvironment(runtimeEnvironment);
  }
}
