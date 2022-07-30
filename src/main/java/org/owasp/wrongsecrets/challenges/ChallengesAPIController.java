package org.owasp.wrongsecrets.challenges;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ChallengesAPIController {

    private final ScoreCard scoreCard;
    private final List<ChallengeUI> challenges;
    private final RuntimeEnvironment runtimeEnvironment;

    private final List<String> descriptions;

    private final List<String> hints;

    public ChallengesAPIController(ScoreCard scoreCard, List<ChallengeUI> challenges, RuntimeEnvironment runtimeEnvironment) {
        this.scoreCard = scoreCard;
        this.challenges = challenges;
        this.runtimeEnvironment = runtimeEnvironment;
        this.descriptions = new ArrayList<>();
        this.hints = new ArrayList<>();
    }


    @GetMapping(value = "/api/challenges", produces = MediaType.APPLICATION_JSON_VALUE)
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
            jsonChallenge.put("category", challenges.get(i).getRuntimeEnvironment());
            jsonChallenge.put("description", descriptions.get(i));
            jsonChallenge.put("hint", hints.get(i));
            jsonChallenge.put("solved", scoreCard.getChallengeCompleted(challenges.get(i).getChallenge()));
            jsonChallenge.put("disabledEnv", getDisabledEnv(challenges.get(i)));
            jsonChallenge.put("difficulty", challenges.get(i).getChallenge().difficulty());
            //hintURL and mitigationURL = not implemented yet
            jsonArray.add(jsonChallenge);
        }
        json.put("status", "success");
        json.put("data", jsonArray);

//      "createdAt": "2022-07-28T16:12:07.564Z",
//      "updatedAt": "2022-07-28T16:12:07.564Z"
        String result = json.toJSONString();
        log.info("returning {}", result);
        return result;
    }


    private void initiaLizeHintsAndDescriptions() {
        log.info("Initialize hints and descriptions");
        challenges.forEach(challengeUI -> {
            String hintsRaw = null;
            try {
                hintsRaw = FileUtils.readFileToString(ResourceUtils.getFile("classpath:explanations/" + challengeUI.getExplanation() + "_hint.adoc"), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String hint = Asciidoctor.Factory.create().convert(hintsRaw, OptionsBuilder.options().build());
            hints.add(hint);

            String descriptionRaw = null;
            try {
                descriptionRaw = FileUtils.readFileToString(ResourceUtils.getFile("classpath:explanations/" + challengeUI.getExplanation() + ".adoc"), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String description = Asciidoctor.Factory.create().convert(descriptionRaw, OptionsBuilder.options().build());
            descriptions.add(description);
        });
    }

    private String getDisabledEnv(ChallengeUI challenge) {
        if (!challenge.getChallenge().supportedRuntimeEnvironments().contains(RuntimeEnvironment.Environment.DOCKER)) {
            return "Docker";
        }
        return null;
    }
}
