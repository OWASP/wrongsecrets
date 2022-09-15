package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.owasp.wrongsecrets.RuntimeEnvironment.Environment;
import org.owasp.wrongsecrets.ScoreCard;

import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class Challenge {

    private final ScoreCard scoreCard;

    public abstract Spoiler spoiler();

    protected abstract boolean answerCorrect(String answer);

    public abstract List<Environment> supportedRuntimeEnvironments();

    public abstract int difficulty();

    public abstract String getTech();

    public abstract boolean isLimittedWhenOnlineHosted();

    public abstract boolean canRunInCTFMode();

    public boolean solved(String answer) {
        var correctAnswer = answerCorrect(answer);
        if (correctAnswer) {
            scoreCard.completeChallenge(this);
        }
        return correctAnswer;
    }

    public String getExplanation() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public String getHint() {
        return this.getClass().getSimpleName().toLowerCase() + "_hint";
    }

    public String getReason() {
        return this.getClass().getSimpleName().toLowerCase() + "_reason";
    }
}
