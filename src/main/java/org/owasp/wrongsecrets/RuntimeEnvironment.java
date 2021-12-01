package org.owasp.wrongsecrets;

import lombok.Getter;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AWS;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.GCP;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.K8S;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.VAULT;

@Component
public class RuntimeEnvironment {

    private static Map<Environment, List<Environment>> envToOverlappingEnvs = Map.of(
            DOCKER, List.of(DOCKER),
            GCP, List.of(DOCKER, K8S, VAULT),
            AWS, List.of(DOCKER, K8S, VAULT),
            VAULT, List.of(DOCKER, K8S),
            K8S, List.of(DOCKER)
    );

    public enum Environment {
        DOCKER("Docker"), GCP("gcp"), AWS("aws"), VAULT("k8s-with-vault"), K8S("k8s");

        private final String id;

        Environment(String id) {
            this.id = id;
        }

        static Environment fromId(String id) {
            return Arrays.asList(Environment.values()).stream().filter(e -> e.id.equalsIgnoreCase(id)).findAny().get();
        }
    }

    @Getter
    private final Environment runtimeEnvironment;

    @Autowired
    public RuntimeEnvironment(@Value("${K8S_ENV}") String currentRuntimeEnvironment) {
        this.runtimeEnvironment = Environment.fromId(currentRuntimeEnvironment);
    }

    public RuntimeEnvironment(Environment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public boolean canRun(Challenge challenge) {
        return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
                || !Collections.disjoint(envToOverlappingEnvs.get(runtimeEnvironment), challenge.supportedRuntimeEnvironments());
    }

}
