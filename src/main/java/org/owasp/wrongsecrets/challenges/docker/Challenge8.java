package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("8")
public class Challenge8 extends Challenge {

    public Challenge8(final ScoreCard scoreCard) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(Constants.newKey);
    }

    @Override
    public boolean answerCorrect(final String answer) {
        return Constants.newKey.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return true;
    }
}
