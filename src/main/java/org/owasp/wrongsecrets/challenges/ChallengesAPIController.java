package org.owasp.wrongsecrets.challenges;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.asciidoc.TemplateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ChallengesAPIController {

    private final ScoreCard scoreCard;
    private final List<ChallengeUI> challenges;

    private final List<String> descriptions;

    private final List<String> hints;

    private final TemplateGenerator templateGenerator;

    public ChallengesAPIController(ScoreCard scoreCard, List<ChallengeUI> challenges, RuntimeEnvironment runtimeEnvironment, TemplateGenerator templateGenerator) {
        this.scoreCard = scoreCard;
        this.challenges = challenges;
        this.descriptions = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.templateGenerator = templateGenerator;
    }


    @GetMapping(value = {"/api/Challenges", "/api/challenges"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getChallenges() {
        if (descriptions.size() == 0) {
            initiaLizeHintsAndDescriptions();
        }
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < challenges.size(); i++) {
            JSONObject jsonChallenge = new JSONObject();
            jsonChallenge.put("id", i);
            jsonChallenge.put("name", challenges.get(i).getName());
            jsonChallenge.put("key", challenges.get(i).getExplanation());
            jsonChallenge.put("category", getCategory(challenges.get(i)));
            jsonChallenge.put("description", descriptions.get(i));
            jsonChallenge.put("hint", hints.get(i));
            jsonChallenge.put("solved", scoreCard.getChallengeCompleted(challenges.get(i).getChallenge()));
            jsonChallenge.put("disabledEnv", getDisabledEnv(challenges.get(i)));
            jsonChallenge.put("difficulty", challenges.get(i).getChallenge().difficulty());
            jsonArray.add(jsonChallenge);
        }
        json.put("status", "success");
        json.put("data", jsonArray);
        String result = json.toJSONString();
        log.info("returning {}", result);
        return result;
    }

    private String getCategory(ChallengeUI challengeUI) {
        return switch (challengeUI.getChallenge().supportedRuntimeEnvironments().get(0)) {
            case DOCKER, HEROKU_DOCKER -> "Docker";
            case GCP, AWS, AZURE -> "Cloud";
            case VAULT -> "Vault";
            case K8S -> "Kubernetes";
        };
    }

    private void initiaLizeHintsAndDescriptions() {
        log.info("Initialize hints and descriptions");
        challenges.forEach(challengeUI -> { //note requires mvn install to generate the html files!
            try {
                String hint = templateGenerator.generate("explanations/" + challengeUI.getExplanation() + "_hint");
                hints.add(hint);
                String description = templateGenerator.generate("explanations/" + challengeUI.getExplanation());
                descriptions.add(description);
            } catch (IOException e) {
                String rawHint = extractResource("classpath:explanations/" + challengeUI.getExplanation() + "_hint.adoc");
                String hint = Asciidoctor.Factory.create().convert(rawHint, OptionsBuilder.options().build());
                hints.add(hint);
                String rawDescription = extractResource("classpath:explanations/" + challengeUI.getExplanation() + ".adoc");
                String description = Asciidoctor.Factory.create().convert(rawDescription, OptionsBuilder.options().build());
                descriptions.add(description);
                throw new RuntimeException(e);
            }

        });
    }

    private String extractResource(String resourceName) {
        try {
            var resource = ResourceUtils.getURL(resourceName);
            final StringBuilder resourceStringbuilder = new StringBuilder();
            new BufferedReader(
                new InputStreamReader(resource.openStream())
            ).lines().forEach(s -> {
                resourceStringbuilder.append(s);
            });
            return resourceStringbuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDisabledEnv(ChallengeUI challenge) {
        if (!challenge.getChallenge().supportedRuntimeEnvironments().contains(RuntimeEnvironment.Environment.DOCKER)) {
            return "Docker";
        }
        return null;
    }
}
