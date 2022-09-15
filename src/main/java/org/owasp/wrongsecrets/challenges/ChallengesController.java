package org.owasp.wrongsecrets.challenges;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.docker.Challenge8;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChallengesController {

    private final ScoreCard scoreCard;
    private final List<ChallengeUI> challenges;
    private final RuntimeEnvironment runtimeEnvironment;

    @Value("${hints_enabled}")
    private boolean hintsEnabled;
    @Value("${reason_enabled}")
    private boolean reasonEnabled;

    @Value("${ctf_enabled}")
    private boolean ctfModeEnabled;

    @Value("${ctf_key}")
    private String ctfKey;

    @Value("challenge_acht_ctf_to_provide_to_host_value")
    private String keyToProvideToHost;


    public ChallengesController(ScoreCard scoreCard, List<ChallengeUI> challenges, RuntimeEnvironment runtimeEnvironment) {
        this.scoreCard = scoreCard;
        this.challenges = challenges;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    @GetMapping
    public String explanation(@PathVariable Integer id) {
        return challenges.get(id - 1).getExplanation();
    }

    @GetMapping("/spoil-{id}")
    public String spoiler(Model model, @PathVariable Integer id) {
        if (!ctfModeEnabled) {
            var challenge = challenges.get(id - 1).getChallenge();
            model.addAttribute("spoiler", challenge.spoiler());
        } else {
            model.addAttribute("spoiler", new Spoiler("Spoils are disabled in CTF mode"));
        }
        return "spoil";
    }

    @GetMapping("/challenge/{id}")
    public String challenge(Model model, @PathVariable Integer id) {
        var challenge = challenges.get(id - 1);

        model.addAttribute("challengeForm", new ChallengeForm(""));
        model.addAttribute("challenge", challenge);

        model.addAttribute("answerCorrect", null);
        model.addAttribute("answerIncorrect", null);
        model.addAttribute("solution", null);
        if (!challenge.isChallengeEnabled()) {
            model.addAttribute("answerIncorrect", "This challenge has been disabled.");
        }
        enrichWithHintsAndReasons(model);
        includeScoringStatus(model, challenge.getChallenge());
        addWarning(challenge.getChallenge(), model);
        fireEnding(model);
        return "challenge";
    }


    @PostMapping(value = "/challenge/{id}", params = "action=reset")
    public String reset(@ModelAttribute ChallengeForm challengeForm, @PathVariable Integer id, Model model) {
        var challenge = challenges.get(id - 1);
        scoreCard.reset(challenge.getChallenge());

        model.addAttribute("challenge", challenge);
        includeScoringStatus(model, challenge.getChallenge());
        addWarning(challenge.getChallenge(), model);
        enrichWithHintsAndReasons(model);
        return "challenge";
    }


    @PostMapping(value = "/challenge/{id}", params = "action=submit")
    public String postController(@ModelAttribute ChallengeForm challengeForm, Model model, @PathVariable Integer id) {
        var challenge = challenges.get(id - 1);

        if (!challenge.isChallengeEnabled()) {
            model.addAttribute("answerIncorrect", "This challenge has been disabled.");
        } else {
            if (challenge.getChallenge().solved(challengeForm.solution())) {
                if (ctfModeEnabled) {
                    String code = generateCode(challenge);
                    if ((challenge.getChallenge() instanceof Challenge8) && (!Strings.isNullOrEmpty(keyToProvideToHost) && !keyToProvideToHost.equals("not_set"))) {
                        model.addAttribute("answerCorrect", "Your answer is correct! " + "fill in the following answer in the CTF instance for which you get your code: " + keyToProvideToHost);
                    }
                    model.addAttribute("answerCorrect", "Your answer is correct! " + "fill in the following code in CTF scoring: " + code);

                } else {
                    model.addAttribute("answerCorrect", "Your answer is correct!");
                }
            } else {
                model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
            }
        }

        model.addAttribute("challenge", challenge);
        includeScoringStatus(model, challenge.getChallenge());
        enrichWithHintsAndReasons(model);
        fireEnding(model);
        return "challenge";
    }

    private String generateCode(ChallengeUI challenge) {
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

    private void includeScoringStatus(Model model, Challenge challenge) {
        model.addAttribute("totalPoints", scoreCard.getTotalReceivedPoints());
        model.addAttribute("progress", "" + scoreCard.getProgress());

        if (scoreCard.getChallengeCompleted(challenge)) {
            model.addAttribute("challengeCompletedAlready", "This exercise is already completed");
        }
    }

    private void addWarning(Challenge challenge, Model model) {
        if (!runtimeEnvironment.canRun(challenge)) {
            var warning = challenge.supportedRuntimeEnvironments().stream()
                .map(Enum::name)
                .limit(1)
                .collect(Collectors.joining());
            model.addAttribute("missingEnvWarning", warning);
        }
    }

    private void enrichWithHintsAndReasons(Model model) {
        model.addAttribute("hintsEnabled", hintsEnabled);
        model.addAttribute("reasonEnabled", reasonEnabled);
    }

    private void fireEnding(Model model) {
        var notCompleted = challenges.stream()
            .filter(ChallengeUI::isChallengeEnabled)
            .map(ChallengeUI::getChallenge)
            .filter(this::challengeNotCompleted)
            .count();
        if (notCompleted == 0) {
            model.addAttribute("allCompleted", "party");
        }
    }

    private boolean challengeNotCompleted(Challenge challenge) {
        return !scoreCard.getChallengeCompleted(challenge);
    }
}
