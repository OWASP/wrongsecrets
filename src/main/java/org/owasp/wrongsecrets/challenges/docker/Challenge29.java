package org.owasp.wrongsecrets.challenges.docker;


import org.bouncycastle.util.encoders.Base64;
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
@Order(29)
public class Challenge29 extends Challenge {

    private final String passcode = new String(Base64.decode("bW9pc2Rmbm93ZXkyMzRmaTMyaWNvOGFxdzQxMg=="));

    public Challenge29(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(passcode);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return passcode.equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    @Override
    public int difficulty() {
        return 1;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.GIT.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

}
