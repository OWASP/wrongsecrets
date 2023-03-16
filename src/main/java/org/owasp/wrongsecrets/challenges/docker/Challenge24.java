package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@Order(24)
public class Challenge24 extends Challenge {

    public Challenge24(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getActualData());
    }

    @Override
    public boolean answerCorrect(String answer) {
        //log.debug("challenge 24, actualdata: {}, answer: {}", getActualData(), answer);
        return getActualData().equals(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    @Override
    public int difficulty() {
        return 2;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.CRYPTOGRAPHY.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    public String getActualData() {
        return new String(Hex.decode("3030303130323033203034303530363037203038303930413042203043304430453046203130313131323133203134313531363137203138313931413142203143314431453146203230323132323233203234323532363237203238323932413242203243324432453246203330333133323333203334333533363337203338333933413342203343334433453346".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

    }
}
