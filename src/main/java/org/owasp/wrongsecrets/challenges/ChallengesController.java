package org.owasp.wrongsecrets.challenges;

import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        var challenge = challenges.get(id - 1).getChallenge();
        model.addAttribute("spoiler", challenge.spoiler());
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

        if (challenge.getChallenge().solved(challengeForm.solution())) {
            model.addAttribute("answerCorrect", "Your answer is correct!");
        } else {
            model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
        }

        model.addAttribute("challenge", challenge);
        includeScoringStatus(model, challenge.getChallenge());
        enrichWithHintsAndReasons(model);
        fireEnding(model);
        return "challenge";
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
            .map(challenge -> challenge.getChallenge())
            .filter(challenge -> !scoreCard.getChallengeCompleted(challenge))
            .count();
        if (notCompleted == 0) {
            model.addAttribute("allCompleted", "party");
        }
    }
}
