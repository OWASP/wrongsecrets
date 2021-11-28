package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("4")
public class Challenge4 extends Challenge {

    public Challenge4(ScoreCard scoreCard) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(Constants.password);
    }

    @Override
    public String getExplanationFile() {
        return "challenge__4__.adoc";
    }

    @Override
    public boolean answerCorrect(String answer) {
        return Constants.password.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return true;
    }
}
