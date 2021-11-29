package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RuntimeEnvironment {

    public enum Environment {
        DOCKER("Docker"), GCP("gcp"), AWS("aws"), VAULT("k8s-with-vault"), K8S("k8s");

        private final String id;

        Environment(String id) {
            this.id = id;
        }

        static Environment fromId(String id) {
            return Arrays.asList(Environment.values()).stream().filter(e -> e.id.equals(id)).findAny().get();
        }
    }

    private final Environment runtimeEnvironment;

    @Autowired
    public RuntimeEnvironment(@Value("${K8S_ENV}") String currentRuntimeEnvironment) {
        this.runtimeEnvironment = Environment.fromId(currentRuntimeEnvironment);
    }

    public RuntimeEnvironment(Environment runtimeEnvironment) {
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public boolean environmentIsFitFor(Challenge challenge) {
        return challenge.supportedRuntimeEnvironments().contains(runtimeEnvironment);
    }

}
