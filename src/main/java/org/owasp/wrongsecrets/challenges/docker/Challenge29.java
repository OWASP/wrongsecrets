package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.spongycastle.util.encoders.Hex;
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

@Component
@Order(29)
public class Challenge29 extends Challenge {

    public Challenge29(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() throws Exception {
        return new Spoiler(decrypt());
    }

    @Override
    public boolean answerCorrect(String answer) throws Exception {
        return decrypt().equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    @Override
    public int difficulty() {
        return 1;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.GIT.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    public String decrypt() throws Exception {
        String privateKeyFilePath = "src/test/resources/RSAprivatekey.pem";
        String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)), StandardCharsets.UTF_8);
        privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "");
        privateKeyContent = privateKeyContent.replace("-----END PRIVATE KEY-----", "");
        privateKeyContent = privateKeyContent.replaceAll("\\s", "");
        byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        byte[] encoded = java.util.Base64.getDecoder().decode("mkS14oW+jxi30ix3YSGjY/roIszGrH5VG2ZBlWzKIQEWiNRZ0C7OtydLk1j0RaQTpt7ZIdUNToAkBkX+GViF8EyGyAf0zfncOf0eDynN8+meA5jajXjDYvooYKqSIcIFycpIiPuvAZqm3Oo7Th6FMqD1ImrdSaNSiooKWNQKbWcMwRxAZlIvnbDBrPXCEHWNSy8RcpwufHXbxqWRsTIxnJsS5NRKcZKBTjRahiHkiwuK7gilrJIQ0rLh+KT4WwroRZ3BvmQytuyIeMbGiQEuQkE9SLVyUX6tmNgLAOITl8QiZ5W8cimmE3KnZBR1klQbxyZc2Xt+YFuEiYMmqa/akg==".getBytes(StandardCharsets.UTF_8));
        byte[] decoded = decode(encoded, privateKey);
        String message = new String(decoded, StandardCharsets.UTF_8);
        return message;
    }

    private static byte[] decode(byte[] encoded, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encoded);
    }
}
