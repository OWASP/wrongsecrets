package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

/**
 * This challenge is about finding a secret in a Github issue (screenshot).
 */
@Component
@Slf4j
@Order(29)
public class Challenge29 extends Challenge {

    public Challenge29(ScoreCard scoreCard) {
        super(scoreCard);
    }

    private static byte[] decode(byte[] encoded, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encoded);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(decryptActualAnswer());
    }

    @Override
    public boolean answerCorrect(String answer) {
        return decryptActualAnswer().equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    /**
     * {@inheritDoc}
     * Difficulty: 1.
     */
    @Override
    public int difficulty() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * Documentation based.
     */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.DOCUMENTATION.id;
    }

    @Override
    public boolean isLimitedWhenOnlineHosted() {
        return false;
    }

    private String decryptActualAnswer() {
        try {
            String privateKeyFilePath = "src/test/resources/RSAprivatekey.pem";
            String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)), StandardCharsets.UTF_8);
            privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "");
            privateKeyContent = privateKeyContent.replace("-----END PRIVATE KEY-----", "");
            privateKeyContent = privateKeyContent.replaceAll("\\s", "");
            byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(privateKeyContent);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(spec);

            byte[] encoded = java.util.Base64.getDecoder().decode("aUb8RPnocWk17xXj0Xag8AOA8K0S4OD/jdqnIzMi5ItpEwPVLZUghYTGx53CHHb2LWRR+WH+Gx41Cr9522FbQDKbDMRaCd7GIMDApwUrFScevI/+usF0bmrw3tH9RUvCtxRZCDtsl038yNn90llsQM1e9OORMIvpzN1Ut0nDKErDvgv4pkUZXqGcybVKEGrULVWiIt8UYzd6lLNrRiRYrbcKrHNveyBhFExLpI/PsWS2NIcqyV7vXIib/PUBH0UdhSVnd+CJhNnFPBxQdScEDK7pYnhctr0I1Vl10Uk86uYsmMzqDSbt+TpCZeofcnd3tPdBB7z3c9ewVS+/fAVwlQ==".getBytes(StandardCharsets.UTF_8));
            byte[] decoded = decode(encoded, privateKey);
            String message = new String(decoded, StandardCharsets.UTF_8);
            return message;
        } catch (Exception e) {
            log.warn("Exception when decrypting: {}", e.getMessage());
            return "wrong_answer";
        }
    }
}
