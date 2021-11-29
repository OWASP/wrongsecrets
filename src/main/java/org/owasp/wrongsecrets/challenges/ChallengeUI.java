package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import org.asciidoctor.Asciidoctor;
import org.owasp.wrongsecrets.RuntimeEnvironment;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.owasp.wrongsecrets.challenges.ChallengeEnvironment.CLOUD;

/**
 * Wrapper class to move logic from Thymeleaf to keep logic in code instead of the html file
 */
@Getter
public class ChallengeUI {

    private static final Asciidoctor asciidoctor = Asciidoctor.Factory.create();
    private static final Pattern challengePattern = Pattern.compile("(\\D+)(\\d+)");

    private final Challenge challenge;
    private final int challengeNumber;
    private final RuntimeEnvironment runtimeEnvironment;

    public ChallengeUI(Challenge challenge, int challengeNumber, RuntimeEnvironment runtimeEnvironment) {
        this.challenge = challenge;
        this.challengeNumber = challengeNumber;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public String getName() {
        var matchers = challengePattern.matcher(challenge.getClass().getSimpleName());
        if (matchers.matches()) {
            return matchers.group(1) + " " + matchers.group(2);
        }
        return "Unknown";
    }

    public Integer getLink() {
        return challengeNumber;
    }

    public String getExplanation() {
        var name = this.getChallenge().getClass().getSimpleName().toLowerCase();
        var env = challenge.getEnvironment() == CLOUD && runtimeEnvironment.equals("gcp") ? "-gcp" : "";

        return String.format("%s%s", name, env);
    }

    public String supportedEnvironments() {
        return challenge.supportedRuntimeEnvironments().stream().map(environment ->
                switch (environment) {
                    case DOCKER -> "Docker";
                    case VAULT -> "Kubernetes or Minikube with Vault";
                    case K8S -> "Kubernetes or Minikube";
                    case AWS, GCP -> "AWS, GCP";
                }
        ).limit(1).collect(Collectors.joining());
    }

    public boolean isChallengeEnabled() {
        return runtimeEnvironment.environmentIsFitFor(challenge);


        //        if ("gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
//            model.addAttribute("cloud", "enabled");
//        }
//        if ("k8s-with-vault".equals(k8sEnvironment) || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
//            model.addAttribute("vault", "enabled");
//        }
//        if (k8sEnvironment.contains("k8s") || "gcp".equals(k8sEnvironment) || "aws".equals(k8sEnvironment)) {
//            model.addAttribute("k8s", "enabled");
//        }
    }

    public static List<ChallengeUI> toUI(List<Challenge> challenges, RuntimeEnvironment environment) {
        return challenges.stream().map(challenge -> new ChallengeUI(challenge, challenges.indexOf(challenge) + 1, environment)).toList();
    }
}
