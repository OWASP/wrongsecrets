package org.owasp.wrongsecrets.challenges;
import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.docker.Challenge8;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.ui.Model;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ChallengeSolver {
    private boolean ctfModeEnabled;
    private String ctfServerAddress;
    private String keyToProvideToHost;

    public ChallengeSolver(boolean ctfModeEnabled, String ctfServerAddress, String keyToProvideToHost) {
        this.ctfModeEnabled = ctfModeEnabled;
        this.ctfServerAddress = ctfServerAddress;
        this.keyToProvideToHost = keyToProvideToHost;
    }
    public void solve(ChallengeForm challengeForm, ChallengeUI challenge, Model model, String ctfKey) {
        if (challenge.getChallenge().solved(challengeForm.solution())) {
            if (ctfModeEnabled) {
                if (!Strings.isNullOrEmpty(ctfServerAddress) && !ctfServerAddress.equals("not_set")) {
                    if (challenge.getChallenge() instanceof Challenge8) {
                        if (!Strings.isNullOrEmpty(keyToProvideToHost) && !keyToProvideToHost.equals("not_set")) { //this means that it was overriden with a code that needs to be returned to the ctf key exchange host.
                            model.addAttribute("answerCorrect", "Your answer is correct! " + "fill in the following answer in the CTF instance at " + ctfServerAddress + "for which you get your code: " + keyToProvideToHost);
                        }
                    } else {
                        model.addAttribute("answerCorrect", "Your answer is correct! " + "fill in the same answer in the ctf-instance of the app: " + ctfServerAddress);
                    }
                } else {
                    String code = generateCode(challenge,ctfKey);
                    model.addAttribute("answerCorrect", "Your answer is correct! " + "fill in the following code in CTF scoring: " + code);
                }
            } else {
                model.addAttribute("answerCorrect", "Your answer is correct!");
            }
        } else {
            model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
        }
    }

    private String generateCode(ChallengeUI challenge,String ctfKey) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(ctfKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(challenge.getName().getBytes(StandardCharsets.UTF_8));
            return new String(Hex.encode(result));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
