package org.owasp.wrongsecrets.challenges;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Used to request and generate the required json for setting up a CTF through juiceshop CTF CLI.
 */
@Slf4j
@RestController
public class ChallengesCtfController {

  private final ScoreCard scoreCard;
  private final Challenges challenges;
  private final ChallengeDefinitionsConfiguration wrongSecretsConfiguration;
  private final RuntimeEnvironment runtimeEnvironment;

  public ChallengesCtfController(
      ScoreCard scoreCard,
      Challenges challenges,
      RuntimeEnvironment runtimeEnvironment,
      ChallengeDefinitionsConfiguration wrongSecretsConfiguration) {
    this.scoreCard = scoreCard;
    this.challenges = challenges;
    this.wrongSecretsConfiguration = wrongSecretsConfiguration;
    this.runtimeEnvironment = runtimeEnvironment;
  }

  @GetMapping(
      value = {"/api/Challenges", "/api/challenges"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Gives all challenges back in a jsonArray, to be used with the Juiceshop CTF cli")
  public String getChallenges() {
    List<ChallengeDefinition> definitions = challenges.getDefinitions().challenges();
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < definitions.size(); i++) {
      ChallengeDefinition definition = definitions.get(i);
      JSONObject jsonChallenge = new JSONObject();
      jsonChallenge.put("id", i + 1);
      jsonChallenge.put("name", definition.name().name());
      jsonChallenge.put("key", definition.name().shortName());
      jsonChallenge.put("category", getCategory() + " - " + definition.category().category());
      jsonChallenge.put(
          "description",
          definition
              .source(runtimeEnvironment)
              .map(s -> s.explanation().contents().get())
              .orElse(null));
      jsonChallenge.put(
          "hint",
          definition.source(runtimeEnvironment).map(s -> s.hint().contents().get()).orElse(null));
      jsonChallenge.put("solved", scoreCard.getChallengeCompleted(definition));
      jsonChallenge.put("disabledEnv", getDisabledEnv(definition));
      jsonChallenge.put("difficulty", definition.difficulty().difficulty());
      jsonArray.add(jsonChallenge);
    }
    json.put("status", "success");
    json.put("data", jsonArray);
    String result = json.toJSONString();
    log.trace("returning {}", result);
    return result;
  }

  private String getCategory() {
    return this.wrongSecretsConfiguration.environments().stream()
        .filter(e -> e.equals(runtimeEnvironment.getRuntimeEnvironment()))
        .findFirst()
        .map(e -> e.ctf())
        .orElse("Unknown");
  }

  private String getDisabledEnv(ChallengeDefinition challengeDefinition) {
    if (runtimeEnvironment.canRun(challengeDefinition)) {
      return runtimeEnvironment.getRuntimeEnvironment().name();
    }
    return null;
  }
}
