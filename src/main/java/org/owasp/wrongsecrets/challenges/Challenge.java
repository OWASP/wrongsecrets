package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.Spoiler;

@RequiredArgsConstructor
@Getter
public abstract class Challenge {

    private final ScoreCard scoreCard;
    private final ChallengeEnvironment environment;

    public abstract Spoiler spoiler();

    public abstract boolean solved(String answer);

    public boolean environmentSupported() {
        return false;
    }

    public String getId() {
        return this.getClass().getSimpleName();
    }

}
