package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import org.asciidoctor.Asciidoctor;

import java.util.List;
import java.util.regex.Pattern;

import static org.owasp.wrongsecrets.challenges.ChallengeEnvironment.CLOUD;

/**
 * Wrapper class to move logic from Thymeleaf to keep logic in code instead of the html file
 */
@Getter
public class ChallengeUI {

    private static final Asciidoctor asciidoctor = Asciidoctor.Factory.create();
    private static final Pattern challengePattern = Pattern.compile("(\\D+)(\\d+)");

    private Challenge challenge;
    private int challengeNumber;
    private String environment;

    public ChallengeUI(Challenge challenge, int challengeNumber, String environment) {
        this.challenge = challenge;
        this.challengeNumber = challengeNumber;
        this.environment = environment;
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
        var env = challenge.getEnvironment() == CLOUD && environment.equals("gcp") ? "-gcp" : "";

        return String.format("%s%s", name, env);
    }

    public static List<ChallengeUI> toUI(List<Challenge> challenges, String environment) {
        return challenges.stream().map(challenge -> new ChallengeUI(challenge, challenges.indexOf(challenge) + 1, environment)).toList();
    }
}
