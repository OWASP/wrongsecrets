package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(21)
@Slf4j
public class Challenge21 extends Challenge {

    private final BinaryExecutionHelper binaryExecutionHelper;

    public Challenge21(ScoreCard scoreCard) {
        super(scoreCard);
        this.binaryExecutionHelper = new BinaryExecutionHelper(21);
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(binaryExecutionHelper.executeGoCommand(""));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return binaryExecutionHelper.executeGoCommand(answer).equals("This is correct! Congrats!");
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }
}
