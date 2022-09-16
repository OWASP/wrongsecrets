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

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(20)
@Slf4j
public class Challenge20 extends Challenge {

    private final BinaryExecutionHelper binaryExecutionHelper;

    public Challenge20(ScoreCard scoreCard) {
        super(scoreCard);
        this.binaryExecutionHelper = new BinaryExecutionHelper(20);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(binaryExecutionHelper.executeCommand("", "wrongsecrets-cplus"));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return binaryExecutionHelper.executeCommand(answer, "wrongsecrets-cplus").equals("This is correct! Congrats!");
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    @Override
    public int difficulty() {
        return 4;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.BINARY.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }
}
