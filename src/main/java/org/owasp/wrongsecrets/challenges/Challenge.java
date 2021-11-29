package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.owasp.wrongsecrets.ScoreCard;

@RequiredArgsConstructor
@Getter
public abstract class Challenge {

    private final ScoreCard scoreCard;
    private final ChallengeEnvironment environment;

    public abstract Spoiler spoiler();

    public String getExplanationFileIdentifier() {
        return this.getClass().getAnnotation(ChallengeNumber.class).value();
    }

    public boolean solved(String answer) {
        var correctAnswer = answerCorrect(answer);
        if (correctAnswer) {
            scoreCard.completeChallenge(this);
        }
        return correctAnswer;
    }

    protected abstract boolean answerCorrect(String answer);

    public boolean environmentSupported() {
        return false;
    }
}
