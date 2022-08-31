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

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.*;

@Component
public class RuntimeEnvironment {

    @Value("${ctf_enabled}")
    private boolean ctfModeEnabled;

    @Value("${SPECIAL_K8S_SECRET}")
    private String challenge5Value; //used to determine if k8s/vault challenges are overriden;

    @Value("${default_aws_value_challenge_9}")
    private String defaultChallenge9Value; //used to determine if the cloud challenge values are overriden

    private static final Map<Environment, List<Environment>> envToOverlappingEnvs = Map.of(
        FLY_DOCKER, List.of(DOCKER, FLY_DOCKER),
        HEROKU_DOCKER, List.of(DOCKER, HEROKU_DOCKER),
        DOCKER, List.of(DOCKER, HEROKU_DOCKER, FLY_DOCKER),
        GCP, List.of(DOCKER, K8S, VAULT),
        AWS, List.of(DOCKER, K8S, VAULT),
        AZURE, List.of(DOCKER, K8S, VAULT),
        VAULT, List.of(DOCKER, K8S),
        K8S, List.of(DOCKER),
        OKTETO_K8S, List.of(K8S, DOCKER, OKTETO_K8S)
    );

    public enum Environment {
        DOCKER("Docker"), HEROKU_DOCKER("Heroku(Docker)"), FLY_DOCKER("Fly(Docker)"), GCP("gcp"), AWS("aws"), AZURE("azure"), VAULT("k8s-with-vault"), K8S("k8s"), OKTETO_K8S("Okteto(k8s)");

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
        String defaultValueChallenge5 = "if_you_see_this_please_use_k8s";
        return ctfModeEnabled && !challenge5Value.equals(defaultValueChallenge5);
    }

    private boolean isCloudUnlockedInCTFMode() {
        String defaultValueAWSValue = "if_you_see_this_please_use_AWS_Setup";
        return ctfModeEnabled && !defaultChallenge9Value.equals(defaultValueAWSValue);
    }

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
        if (isK8sUnlockedInCTFMode()) {
            return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
                || challenge.supportedRuntimeEnvironments().contains(DOCKER) || challenge.supportedRuntimeEnvironments().contains(K8S)
                || challenge.supportedRuntimeEnvironments().contains(VAULT);
        }
        return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment)
            || !Collections.disjoint(envToOverlappingEnvs.get(runtimeEnvironment), challenge.supportedRuntimeEnvironments());
    }

}
