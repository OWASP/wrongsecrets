package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry;
import org.linguafranca.pwdb.kdbx.simple.SimpleGroup;
import org.linguafranca.pwdb.kdbx.simple.SimpleIcon;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class Challenge14 extends Challenge {

    private final String keepassxPassword;
    private final String defaultKeepassValue;

    @Override
    public Spoiler spoiler() {
        return new Spoiler(findAnswer());
    }

    public Challenge14(ScoreCard scoreCard, @Value("${keepasxpassword}") String keepassxPassword, @Value("${KEEPASS_BROKEN}") String defaultKeepassValue) {
        super(scoreCard);
        this.keepassxPassword = keepassxPassword;
        this.defaultKeepassValue = defaultKeepassValue;
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return isanswerCorrectInKeeyPassx(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    private String findAnswer() {
        if (Strings.isEmpty(keepassxPassword)) {
            log.info("Checking secret with values {}", keepassxPassword);
            return defaultKeepassValue;
        }
        try {
            KdbxCreds creds = new KdbxCreds(keepassxPassword.getBytes());
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test1.kdbx");
            Database<SimpleDatabase, SimpleGroup, SimpleEntry, SimpleIcon> database = SimpleDatabase.load(creds, inputStream);
            return database.findEntries("alibaba").stream().findFirst().toString();
        } catch (Exception e) {
            log.error("Exception with challnege 16", e);
            return defaultKeepassValue;
        }
    }

    //todo: write a test!
    private boolean isanswerCorrectInKeeyPassx(String answer) {
        if (Strings.isEmpty(keepassxPassword) || Strings.isEmpty(answer)) {
            log.info("Checking secret with values {}, {}", keepassxPassword, answer);
            return false;
        }
        return answer.equals(findAnswer());
    }
}
