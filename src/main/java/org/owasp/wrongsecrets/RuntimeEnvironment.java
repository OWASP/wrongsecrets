package org.owasp.wrongsecrets;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class establishing whether a challenge can run or not depending on the given RuntimeEnvironment
 * and whether components are configured and the CTFmode is enabled or not.
 */
@Component
public class RuntimeEnvironment {

  @Value("${ctf_enabled}")
  private boolean ctfModeEnabled;

  @Value("${SPECIAL_K8S_SECRET}")
  private String challenge5Value; // used to determine if k8s/vault challenges are overriden;

  @Value("${vaultpassword}")
  private String challenge7Value;

  @Value("${default_aws_value_challenge_9}")
  private String
      defaultChallenge9Value; // used to determine if the csloud challenge values are overriden

  private static final Map<Environment, List<Environment>> envToOverlappingEnvs =
      Map.of(
          FLY_DOCKER,
          List.of(DOCKER, FLY_DOCKER),
          HEROKU_DOCKER,
          List.of(DOCKER, HEROKU_DOCKER),
          DOCKER,
          List.of(DOCKER, HEROKU_DOCKER, FLY_DOCKER),
          GCP,
          List.of(DOCKER, K8S, VAULT),
          AWS,
          List.of(DOCKER, K8S, VAULT),
          AZURE,
          List.of(DOCKER, K8S, VAULT),
          VAULT,
          List.of(DOCKER, K8S),
          K8S,
          List.of(DOCKER),
          OKTETO_K8S,
          List.of(K8S, DOCKER, OKTETO_K8S));

  /** Enum with possible environments supported by the app. */
  public enum Environment {
    DOCKER("Docker"),
    HEROKU_DOCKER("Heroku(Docker)"),
    FLY_DOCKER("Fly(Docker)"),
    GCP("gcp"),
    AWS("aws"),
    AZURE("azure"),
    VAULT("k8s-with-vault"),
    K8S("k8s"),
    OKTETO_K8S("Okteto(k8s)");

    private final String id;

    Environment(String id) {
      this.id = id;
    }

    static Environment fromId(String id) {
      return Arrays.stream(Environment.values())
          .filter(e -> e.id.equalsIgnoreCase(id))
          .findAny()
          .get();
    }
  }

  @Getter private final Environment runtimeEnvironment;

  @Autowired
  public RuntimeEnvironment(@Value("${K8S_ENV}") String currentRuntimeEnvironment) {
    this.runtimeEnvironment = Environment.fromId(currentRuntimeEnvironment);
  }

  public RuntimeEnvironment(Environment runtimeEnvironment) {
    this.runtimeEnvironment = runtimeEnvironment;
  }

  public boolean canRun(Challenge challenge) {
    if (isCloudUnlockedInCTFMode()) {
      return true;
    }
    if (isVaultUnlockedInCTFMode() && isK8sUnlockedInCTFMode()) {
      return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
          || challenge.supportedRuntimeEnvironments().contains(DOCKER)
          || challenge.supportedRuntimeEnvironments().contains(K8S)
          || challenge.supportedRuntimeEnvironments().contains(VAULT);
    }
    if (isK8sUnlockedInCTFMode()) {
      return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
          || challenge.supportedRuntimeEnvironments().contains(DOCKER)
          || challenge.supportedRuntimeEnvironments().contains(K8S);
    }
    return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
        || !Collections.disjoint(
            envToOverlappingEnvs.get(runtimeEnvironment), challenge.supportedRuntimeEnvironments());
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
}
