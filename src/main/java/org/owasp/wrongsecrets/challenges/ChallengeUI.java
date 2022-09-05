package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import org.owasp.wrongsecrets.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Wrapper class to move logic from Thymeleaf to keep logic in code instead of the html file.
 */
@Getter
public class ChallengeUI {

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

    public String getTech() {
        return challenge.getTech();
    }

    public Integer next() {
        return challengeNumber + 1;
    }

    public Integer previous() {
        return challengeNumber - 1;
    }

    public String getExplanation() {
        return challenge.getExplanation();
    }

    public String getHint() {
        List<RuntimeEnvironment.Environment> limittedEnvs = List.of(RuntimeEnvironment.Environment.HEROKU_DOCKER, RuntimeEnvironment.Environment.FLY_DOCKER, RuntimeEnvironment.Environment.OKTETO_K8S);
        if (limittedEnvs.contains(runtimeEnvironment.getRuntimeEnvironment()) && challenge.isLimittedWhenOnlineHosted()) {
            return challenge.getHint() + "_limitted";
        }
        return challenge.getHint();
    }

    public String getReason() {
        return challenge.getReason();
    }

    public String requiredEnv() {
        return challenge.supportedRuntimeEnvironments().stream()
            .map(Enum::name)
            .limit(1)
            .collect(Collectors.joining());
    }

    public int difficulty() {
        return challenge.difficulty();
    }

    public boolean isChallengeEnabled() {
        return runtimeEnvironment.canRun(challenge);
    }

    public static List<ChallengeUI> toUI(List<Challenge> challenges, RuntimeEnvironment environment) {
        return challenges.stream()
            .sorted(Comparator.comparingInt(challenge -> Integer.parseInt(challenge.getClass().getSimpleName().replace("Challenge", ""))))
            .map(challenge -> new ChallengeUI(challenge, challenges.indexOf(challenge) + 1, environment))
            .toList();
    }
}
