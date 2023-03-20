package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Component
@Order(29)
@RestController
public class Challenge29 extends Challenge {

    @GetMapping("/localStorageString")
    public String getMyString() {
        return "ThisIsYourPasswordOfChallenge30";
    }

    public String solution="ThisIsYourPasswordOfChallenge30";

    public Challenge29(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(solution);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return solution.equals(answer);
    }



    @Override
    public int difficulty() {
        return 2;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.FRONTEND.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }



    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }





}
