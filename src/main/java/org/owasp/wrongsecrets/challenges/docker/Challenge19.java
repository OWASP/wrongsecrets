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
@Order(19)
@Slf4j
public class Challenge19 extends Challenge {

    private final BinaryExecutionHelper binaryExecutionHelper;

    public Challenge19(ScoreCard scoreCard) {
        super(scoreCard);
        this.binaryExecutionHelper = new BinaryExecutionHelper(19);
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(binaryExecutionHelper.executeCommand("", "wrongsecrets-c"));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return binaryExecutionHelper.executeCommand(answer, "wrongsecrets-c").equals("This is correct! Congrats!");
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }
}
