package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import org.asciidoctor.Asciidoctor;
import org.owasp.wrongsecrets.RuntimeEnvironment;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Wrapper class to move logic from Thymeleaf to keep logic in code instead of the html file
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

    public boolean isChallengeEnabled() {
        return runtimeEnvironment.canRun(challenge);
    }

    public static List<ChallengeUI> toUI(List<Challenge> challenges, RuntimeEnvironment environment) {
        return challenges.stream().map(challenge -> new ChallengeUI(challenge, challenges.indexOf(challenge) + 1, environment)).toList();
    }
}
