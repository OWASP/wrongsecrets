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

    public boolean solved(String answer) {
        var correctAnswer = answerCorrect(answer);
        if (correctAnswer) {
            scoreCard.completeChallenge(this);
        }
        return correctAnswer;
    }
}
