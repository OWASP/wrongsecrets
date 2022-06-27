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
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.AZURE;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.K8S;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.VAULT;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.HEROKU_DOCKER;

@Component
public class RuntimeEnvironment {

    private static final Map<Environment, List<Environment>> envToOverlappingEnvs = Map.of(
            HEROKU_DOCKER, List.of(DOCKER, HEROKU_DOCKER),
            DOCKER, List.of(DOCKER, HEROKU_DOCKER),
            GCP, List.of(DOCKER, K8S, VAULT),
            AWS, List.of(DOCKER, K8S, VAULT),
            AZURE, List.of(DOCKER, K8S, VAULT),
            VAULT, List.of(DOCKER, K8S),
            K8S, List.of(DOCKER)
    );

    public enum Environment {
        DOCKER("Docker"), HEROKU_DOCKER("Heroku(Docker)"), GCP("gcp"), AWS("aws"), AZURE("azure"), VAULT("k8s-with-vault"), K8S("k8s");

        private final String id;

        Environment(String id) {
            this.id = id;
        }

        static Environment fromId(String id) {
            return Arrays.stream(Environment.values()).filter(e -> e.id.equalsIgnoreCase(id)).findAny().get();
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
