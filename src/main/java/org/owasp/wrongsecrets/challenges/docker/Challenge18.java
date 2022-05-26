package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(18)
public class Challenge18 extends Challenge {

    private final String hashPassword;

    public Challenge18(ScoreCard scoreCard, @Value("aHVudGVyMg==") String hashPassword) {
        super(scoreCard);
        this.hashPassword = hashPassword;
    }

    private String base64Decode(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes);
    }

    private String calculateHash(String hash) {
        if ("md5".equals(hash)) {
            return DigestUtils.md5Hex(base64Decode(hashPassword));
        }
        else if ("sha1".equals(hash)) {
            return DigestUtils.sha1Hex(base64Decode(hashPassword));
        }
        else {
            return "No Hash Selected";
        }
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(base64Decode(hashPassword));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return calculateHash("md5").equals(DigestUtils.md5Hex(answer)) 
        || calculateHash("sha1").equals(DigestUtils.sha1Hex(answer));
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }
}
