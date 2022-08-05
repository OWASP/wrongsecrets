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

    @Value("${ctf_enabled}")
    private boolean ctf_mode_enabled;
    private final String defaultValueChallenge5 = "if_you_see_this_please_use_k8s";

    @Value("${SPECIAL_K8S_SECRET}")
    private String challenge5Value; //used to determine if they are overriden;

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

    private boolean isK8sUnlockedInCTFMode() {
        return ctf_mode_enabled && !challenge5Value.equals(defaultValueChallenge5);
    }

    @Autowired
    public RuntimeEnvironment(@Value("${K8S_ENV}") String currentRuntimeEnvironment) {
        this.runtimeEnvironment = Environment.fromId(currentRuntimeEnvironment);
    }

    public RuntimeEnvironment(Environment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public boolean canRun(Challenge challenge) {
        if (isK8sUnlockedInCTFMode()) {
            return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment) || challenge.supportedRuntimeEnvironments().contains(K8S) || challenge.supportedRuntimeEnvironments().contains(VAULT);
        } //TODO: WRITE TEST FOR THIS & UPDATE UI THEN!
        return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
            || !Collections.disjoint(envToOverlappingEnvs.get(runtimeEnvironment), challenge.supportedRuntimeEnvironments());
    }

}
