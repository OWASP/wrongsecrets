package org.owasp.wrongsecrets.challenges;

import org.owasp.wrongsecrets.ScoreCard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ChallengesController {

    private final String version;
    private final ScoreCard scoreCard;
    private final List<Challenge> challenges;

    public ChallengesController(@Value("${APP_VERSION}") String version, ScoreCard scoreCard, List<Challenge> challenges) {
        this.version = version;
        this.scoreCard = scoreCard;
        this.challenges = challenges;
    }

    /**
     * UI works with challenge numbers, to make an easy mapping between challenges and numbers we use the ChallengeNumber
     * annotation on Challenge classes. This way it stays limited to the controller and the order of challenges can easily
     * be changed
     */
    private Challenge findChallenge(String id) {
        return challenges.stream()
                .filter(c -> c.getClass().getAnnotation(ChallengeNumber.class).value().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Challenge " + id + " + not found, did you add the annotation?"));
    }

    private Integer challengeNumber(Challenge challenge) {
        return Integer.valueOf(challenge.getClass().getAnnotation(ChallengeNumber.class).value());
    }

    @GetMapping("/spoil-{id:1|2}") //TODO needed for migration
    public String spoiler(Model model, @PathVariable String id) {
        var challenge = findChallenge(id);
        model.addAttribute("solution", challenge.spoiler().solution()); //TODO update spoiler class directly instead of the String
        return "spoil";
    }

    @GetMapping("/challenge/{id:1|2}") //TODO needed for migration
    public String challenge(Model model, @PathVariable String id) {
        var challenge = findChallenge(id);

        model.addAttribute("challengeForm", new ChallengeForm(""));
        model.addAttribute("answerCorrect", null);
        model.addAttribute("answerIncorrect", null);
        model.addAttribute("solution", null);
        model.addAttribute("challengeNumber", challengeNumber(challenge));

        includeScoringStatus(model, challenge);
        addWarning(challenge, model);

        return "challenge";
    }

    @PostMapping("/challenge/{id:1|2}")
    public String postController(@ModelAttribute ChallengeForm challengeForm, Model model, @PathVariable String id) {
        var challenge = findChallenge(id);
        model.addAttribute("challengeNumber", challengeNumber(challenge));

        if (challenge.solved(challengeForm.solution())) {
            model.addAttribute("answerCorrect", "Your answer is correct!");
        } else {
            model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
        }
        includeScoringStatus(model, challenge);
        addWarning(challenge, model);
        return "challenge";
    }

    private void includeScoringStatus(Model model, Challenge challenge) {
        model.addAttribute("version", version);
        model.addAttribute("totalPoints", scoreCard.getTotalReceivedPoints());
        model.addAttribute("progress", "" + scoreCard.getProgress());

        if (scoreCard.getChallengeCompleted(challenge.getId())) {
            model.addAttribute("challengeCompletedAlready", "This exercise is already completed");
        }
    }

    private void addWarning(Challenge challenge, Model model) {
        if (!challenge.environmentSupported())
            model.addAttribute("runtimeWarning", switch (challenge.getEnvironment()) {
                case DOCKER -> "We are running outside of a docker container. Please run this in a container as explained in the README.md.";
                default -> "??";
            });
    }
}
