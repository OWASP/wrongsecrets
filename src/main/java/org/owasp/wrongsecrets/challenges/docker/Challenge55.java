package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

/** Challenge with a secret in .ssh/config */
@Slf4j
@Component
public class Challenge55 implements Challenge {

    private final String basionhostpath;
    public Challenge55(@Value("${basionhostpath}") String basionhostpath) {
        this.basionhostpath = basionhostpath;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getActualData());
    }

    @Override
    public boolean answerCorrect(String answer) {
        return !Strings.isNullOrEmpty(answer) && (answer.contains(getActualData()) || getActualData().contains(answer));
    }

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "The location of the basionhostpath is based on an Env Var")
    private String getActualData() {
        try {
            return Files.readString(Paths.get(basionhostpath, "wrongsecrets.keys"), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn(
                "Exception during file reading, defaulting to default without a docker container"
                    + " environment",
                e);
            return FILE_MOUNT_ERROR;
        }
    }
}
