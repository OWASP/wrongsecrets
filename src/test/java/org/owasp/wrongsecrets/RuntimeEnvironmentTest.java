package org.owasp.wrongsecrets;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AWS;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.GCP;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.K8S;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.VAULT;

import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.RuntimeEnvironment.Environment;
import org.owasp.wrongsecrets.challenges.Challenge;

@ExtendWith(MockitoExtension.class)
class RuntimeEnvironmentTest {

  @Mock private Challenge challenge;

  @MethodSource("runtimeToChallengeEnvironments")
  private static Stream<Arguments> runtimeEnvToChallengeEnvironments() {
    return Stream.of(
        Arguments.of(DOCKER, List.of(DOCKER), true),
        Arguments.of(DOCKER, List.of(K8S, VAULT, AWS, GCP), false),
        Arguments.of(K8S, List.of(DOCKER, K8S), true),
        Arguments.of(K8S, List.of(VAULT, AWS, GCP), false),
        Arguments.of(VAULT, List.of(DOCKER, K8S, VAULT), true),
        Arguments.of(VAULT, List.of(AWS, GCP), false),
        Arguments.of(AWS, List.of(DOCKER, K8S, VAULT, AWS), true),
        Arguments.of(AWS, List.of(GCP), false),
        Arguments.of(GCP, List.of(DOCKER, K8S, VAULT, GCP), true),
        Arguments.of(GCP, List.of(AWS), false),
        Arguments.of(GCP, List.of(DOCKER, K8S, VAULT, GCP, AWS), true),
        Arguments.of(AWS, List.of(DOCKER, K8S, VAULT, GCP, AWS), true));
  }

  @ParameterizedTest
  @MethodSource("runtimeEnvToChallengeEnvironments")
  void checkEnvToChallenge(
      Environment runtimeEnvironment, List<Environment> challengeSupports, boolean expected) {
    var runtimeEnv = new RuntimeEnvironment(runtimeEnvironment);
    Mockito.when(challenge.supportedRuntimeEnvironments()).thenReturn(challengeSupports);

    Assertions.assertThat(runtimeEnv.canRun(challenge)).isEqualTo(expected);
  }
}
