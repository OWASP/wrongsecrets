package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("12")
public class Challenge12 extends Challenge {

    public Challenge12(ScoreCard scoreCard) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getActualData());
    }

    @Override
    public boolean answerCorrect(String answer) {
        return getActualData().equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return !"if_you_see_this_please_use_docker_instead".equals(getActualData());
    }


    private String getActualData() {
        //do logic here!
        
        return "if_you_see_this_please_use_docker_instead";
    }
}
