package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@Order(14)
public class Challenge14 extends Challenge {

    private final String keepassxPassword;
    private final String defaultKeepassValue;
    private final String filePath;

    public Challenge14(ScoreCard scoreCard, @Value("${keepasxpassword}") String keepassxPassword,
                       @Value("${KEEPASS_BROKEN}") String defaultKeepassValue,
                       @Value("${keepasspath}") String filePath) {
        super(scoreCard);
        this.keepassxPassword = keepassxPassword;
        this.defaultKeepassValue = defaultKeepassValue;
        this.filePath = filePath;
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(findAnswer());
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return isanswerCorrectInKeeyPassx(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    @Override
    public int difficulty() {
        return 4;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.PASSWORD_MANAGER.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private String findAnswer() {
        if (Strings.isEmpty(keepassxPassword)) {
            //log.debug("Checking secret with values {}", keepassxPassword);
            return defaultKeepassValue;
        }
        KdbxCreds creds = new KdbxCreds(keepassxPassword.getBytes(StandardCharsets.UTF_8));
        Database<SimpleDatabase, SimpleGroup, SimpleEntry, SimpleIcon> database;


        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            database = SimpleDatabase.load(creds, inputStream);
            return database.findEntries("alibaba").get(0).getPassword();
        } catch (Exception | Error e) {
            log.error("Exception or Error with Challenge 14", e);
            return defaultKeepassValue;
        }
    }

    private boolean isanswerCorrectInKeeyPassx(String answer) {
        if (Strings.isEmpty(keepassxPassword) || Strings.isEmpty(answer)) {
            //log.debug("Checking secret with values {}, {}", keepassxPassword, answer);
            return false;
        }
        return answer.equals(findAnswer());
    }

}
