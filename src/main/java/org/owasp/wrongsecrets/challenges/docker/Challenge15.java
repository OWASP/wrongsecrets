package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(15)
public class Challenge15 extends Challenge {

    public Challenge15(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public Spoiler spoiler() {
        return null;
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return false;
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    /**
     * Arcane:
     * [Arcane]
     * aws_access_key_id = AKIAYVP4CIPPEMEC27B2
     * aws_secret_access_key = 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
     * output = json
     * region = us-east-2
     *
     * Arcane debug:
     * [default]
     * aws_access_key_id = AKIAYVP4CIPPJCJOPJWL
     * aws_secret_access_key = 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
     * output = json
     * region = us-east-2
     *
     * wrongsecrets debug:
     * [default]
     * aws_access_key_id = AKIAYVP4CIPPCXOWVNMW
     * aws_secret_access_key = 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
     * output = json
     * region = us-east-2
     */
}
