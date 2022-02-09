package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class Challenge16 extends Challenge {

    private String keepassxPassword;

    @Override
    public Spoiler spoiler() {
        return null;
    }

    public Challenge16(ScoreCard scoreCard, @Value("${plainText13}") String keepassxPassword) {
        super(scoreCard);
        this.keepassxPassword = keepassxPassword;
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return isanswerCorrectInKeeyPassx(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    private boolean isanswerCorrectInKeeyPassx(String answer) {
        if (Strings.isEmpty(keepassxPassword) || Strings.isEmpty(answer)) {
            log.info("Checking secret with values {}, {}", keepassxPassword, answer);
            return false;
        }

        try {
            KdbxCreds creds = new KdbxCreds(keepassxPassword.getBytes());
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test1.kdbx");
            Database database = SimpleDatabase.load(creds, inputStream);
            //todo implement keepassreading!
            return false;
        } catch (Exception e) {
            log.error("Exception with challnege 16", e);
            return false;
        }

    }
}
