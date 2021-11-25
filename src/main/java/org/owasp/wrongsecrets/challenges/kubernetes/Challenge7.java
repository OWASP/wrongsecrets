package org.owasp.wrongsecrets.challenges.kubernetes;


import org.apache.logging.log4j.util.Strings;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("7")
public class Challenge7 extends Challenge {

    private final Vaultpassword vaultPassword;
    private final String vaultPasswordString;

    public Challenge7(final ScoreCard scoreCard,
                      final Vaultpassword vaultPassword, @Value("${vaultpassword}") String vaultPasswordString) {
        super(scoreCard, ChallengeEnvironment.K8S_VAULT);
        this.vaultPassword = vaultPassword;
        this.vaultPasswordString = vaultPasswordString;
    }

    private String getAnswer() {
        return vaultPassword != null && Strings.isNotEmpty(vaultPassword.getPasssword()) ? vaultPassword.getPasssword() : vaultPasswordString;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getAnswer());
    }

    @Override
    public boolean answerCorrect(final String answer) {
        return getAnswer().equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return vaultPassword != null && vaultPassword.getPasssword() != null;
    }
}
