package org.owasp.wrongsecrets.challenges;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.asciidoc.TemplateGenerator;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Used to request and generate the required json for setting up a CTF through juiceshop CTF CLI.
 */
@Slf4j
@RestController
public class ChallengesAPIController {

  private final ScoreCard scoreCard;
  private final List<ChallengeUI> challenges;

  private final List<String> descriptions;

  private final List<String> hints;

  private final TemplateGenerator templateGenerator;

  private final RuntimeEnvironment runtimeEnvironment;

  public ChallengesAPIController(
      ScoreCard scoreCard,
      List<ChallengeUI> challenges,
      RuntimeEnvironment runtimeEnvironment,
      TemplateGenerator templateGenerator) {
    this.scoreCard = scoreCard;
    this.challenges = challenges;
    this.descriptions = new ArrayList<>();
    this.hints = new ArrayList<>();
    this.runtimeEnvironment = runtimeEnvironment;
    this.templateGenerator = templateGenerator;
  }

  @GetMapping(
      value = {"/api/Challenges", "/api/challenges"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Gives all challenges back in a jsonArray, to be used with the Juiceshop CTF cli")
  public String getChallenges() {
    if (descriptions.size() == 0) {
      initializeHintsAndDescriptions();
    }
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < challenges.size(); i++) {
      JSONObject jsonChallenge = new JSONObject();
      jsonChallenge.put("id", i + 1);
      jsonChallenge.put("name", challenges.get(i).getName());
      jsonChallenge.put("key", challenges.get(i).getExplanation());
      jsonChallenge.put(
          "category", getCategory(challenges.get(i)) + " - " + challenges.get(i).getTech());
      jsonChallenge.put("description", descriptions.get(i));
      jsonChallenge.put("hint", hints.get(i));
      jsonChallenge.put(
          "solved", scoreCard.getChallengeCompleted(challenges.get(i).getChallenge()));
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
      case DOCKER, HEROKU_DOCKER, FLY_DOCKER -> "Docker";
      case GCP, AWS, AZURE -> "Cloud";
      case VAULT -> "Vault";
      case K8S, OKTETO_K8S -> "Kubernetes";
    };
  }

  private void initializeHintsAndDescriptions() {
    log.info("Initialize hints and descriptions");
    challenges.forEach(
        challengeUI -> { // note requires mvn install to generate the html files!
          try {
            String hint =
                templateGenerator.generate(
                    "explanations/" + challengeUI.getExplanation() + "_hint");
            hints.add(hint);
            String description =
                templateGenerator.generate("explanations/" + challengeUI.getExplanation());
            descriptions.add(description);
          } catch (IOException e) {
            String rawHint =
                extractResource(
                    "classpath:explanations/" + challengeUI.getExplanation() + "_hint.adoc");
            try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
              String hint = asciidoctor.convert(rawHint, Options.builder().build());
              hints.add(hint);
            }
            String rawDescription =
                extractResource("classpath:explanations/" + challengeUI.getExplanation() + ".adoc");
            try (Asciidoctor asciidoctor = Asciidoctor.Factory.create()) {
              String description = asciidoctor.convert(rawDescription, Options.builder().build());
              descriptions.add(description);
            }
            throw new RuntimeException(e);
          }
        });
  }

  @SuppressFBWarnings(
      value = "URLCONNECTION_SSRF_FD",
      justification = "Read from specific classpath")
  private String extractResource(String resourceName) {
    try {
      var resource = ResourceUtils.getURL(resourceName);
      final StringBuilder resourceStringbuilder = new StringBuilder();
      try (BufferedReader bufferedReader =
          new BufferedReader(
              new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8))) {
        bufferedReader.lines().forEach(resourceStringbuilder::append);
        return resourceStringbuilder.toString();
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getDisabledEnv(ChallengeUI challenge) {
    if (runtimeEnvironment.canRun(challenge.getChallenge())) {
      return runtimeEnvironment.getRuntimeEnvironment().name();
    }
    return null;
  }
}
