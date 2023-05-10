package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;

/**
 * This is a challenge based on LLM where poeople need to extract the secret from https://gpa.43z.one/
 */
@Slf4j
@Component
@Order(32)
public class Challenge32 extends Challenge {


    public Challenge32(ScoreCard scoreCard) {
        super(scoreCard);
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


    /**
     * {@inheritDoc}
     */
    @Override
    public int difficulty() {
        return Difficulty.NORMAL;
    }

    /**
     * {@inheritDoc}
     * This is a front-end / web type of challenge
     */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.AI.id;
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
        return decrypt(decrypt(decrypt("daBa42GnqZs8FtRZ4qmbURWt5+WUbibEE7hOoLV6J2d+zRth6GUzaQx+7N2KjofhoJmO4/io9jgcGdH8FKZrddnH8jWIMtd7hTXlnIST/CqcO5h5ir3HgLaQ863QRr3LGycvcaBU99vZB+ofm48JQa4F8DFSfrf0RIjwcQ==")));
    }

    private String decrypt(String cipherTextString) {
        try {
            final Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey decryptKey = new SecretKeySpec("AIKnowsThisKey12".getBytes(StandardCharsets.UTF_8), "AES");
            AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, Base64.decode(cipherTextString), 0, 12);
            decryptor.init(Cipher.DECRYPT_MODE, decryptKey, gcmIv);
            return new String(decryptor.doFinal(Base64.decode(cipherTextString), 12, Base64.decode(cipherTextString).length - 12));
        } catch (Exception e) {
            log.warn("Exception in Challenge32", e);
            return "";
        }

    }


}
