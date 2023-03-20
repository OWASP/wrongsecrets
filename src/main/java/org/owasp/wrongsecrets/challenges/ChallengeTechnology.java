package org.owasp.wrongsecrets.challenges;

import java.util.Arrays;

public class ChallengeTechnology {

    public enum Tech {

        GIT("Git"), DOCKER("Docker"), CONFIGMAPS("Configmaps"), SECRETS("Secrets"), VAULT("Vault"), LOGGING("Logging"), TERRAFORM("Terraform"), CSI("CSI-Driver"), CICD("CI/CD"), PASSWORD_MANAGER("Password Manager"), CRYPTOGRAPHY("Cryptography"), BINARY("Binary"), FRONTEND("Front-end"), IAM("IAM privilege escalation"), WEB3("Web3"), DOCUMENTATION("Documentation");
        public final String id;

        Tech(String id) {
            this.id = id;
        }

        static ChallengeTechnology.Tech fromId(String id) {
            return Arrays.stream(ChallengeTechnology.Tech.values()).filter(e -> e.id.equalsIgnoreCase(id)).findAny().get();
        }
    }
}
