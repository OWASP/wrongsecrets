package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(28) //make sure this number is the same as your challenge
public class Challenge28 extends Challenge {
    private final String secret;

    public Challenge28(ScoreCard scoreCard) {
        super(scoreCard);
        secret = "hello world";
    }

    //is this challenge usable in CTF mode?
    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    //return the plain text secret here
    @Override
    public Spoiler spoiler() {
        return new Spoiler(secret);
    }


    //here you validate if your answer matches the secret
    @Override
    public boolean answerCorrect(String answer) {
        return secret.equals(answer);
    }

    //which runtime can you use to run the challenge on ? (You can just use Docker here)
    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    //set the difficulty: 1=low, 5=very hard
    @Override
    public int difficulty() {
        return 1;
    }

    //on which tech is this challenge? See ChallengeTechnology.Tech for categories
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.SECRETS.id;
    }

    //if you use this in a shared environment and need to adapt it, then return true here.
    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

}