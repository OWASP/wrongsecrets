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

        byte[] encoded = java.util.Base64.getDecoder().decode("jA5Y9UJAgXa/En5wOAgnP5E9VCw6IZ/snbm20iGW0NKjxVzdIPvCeJoYyyI5KZ3snhRCRzD0SAoKO5FUyz8Rniw/tWVTzNEh76wLMVZ3STDAO5gF78qAp3/dfseWgVEAL4Y/B9ESNWftglTop12y1DIc3luOK8VEZjRC7eVDAP4kA72eTl2M2AvGqFEKVOjnQFh5My3nazUkWMjy5wrLdRjthDlyMB4NEatkfU5EE7dDyvblJTqz2/dEzuDtWpO1RRim0UoxnSqsKCMAyhKwObS5uGS4kkStLLZijdvsrvB63/LlbksFGPEexVJvplJOzG6g9buTdKDf0IoUKCyimw==".getBytes(StandardCharsets.UTF_8));
        byte[] decoded = decode(encoded, privateKey);
        String message = new String(decoded, StandardCharsets.UTF_8);
        return message;
    }

    private static byte[] decode(byte[] encoded, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encoded);
    }
}
