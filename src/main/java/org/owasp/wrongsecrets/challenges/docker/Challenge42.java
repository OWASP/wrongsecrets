package org.owasp.wrongsecrets.challenges.docker;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.XMLConfiguration;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on leaking secrets due to keeping the encryption key and secret
 * together
 */
@Slf4j
@Component
@Order(42)
public class Challenge42 extends Challenge {

    private final Resource resource;

    public Challenge42(
        ScoreCard scoreCard, @Value("classpath:maven/settings/settings.xml") Resource resource) {
        super(scoreCard);
        this.resource = resource;
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getSolution());
    }

    @Override
    public boolean answerCorrect(String answer) {
        return getSolution().equals(answer);
    }

    /** {@inheritDoc} */
    @Override
    public int difficulty() {
        return Difficulty.EASY;
    }

    /** {@inheritDoc} Cryptography based. */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.CRYPTOGRAPHY.id;
    }

    @Override
    public boolean isLimitedWhenOnlineHosted() {
        return false;
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    private String getSolution() {
        try {
            String config = resource.getContentAsString(Charset.defaultCharset());
            StringReader stringReader = new StringReader(config);

            XMLConfiguration xmlConfiguration = new XMLConfiguration();
            xmlConfiguration.read(stringReader);

            // Retrieve the Nexus password
            return xmlConfiguration.getString("nexus.password");
        } catch (Exception e) {
            log.warn("there was an exception with decrypting content in challenge42", e);
            return "error_decryption";
        }
    }
}
