package org.owasp.wrongsecrets;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.owasp.wrongsecrets.challenges.ChallengeUI;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Make sure shared model properties are always set for each controller. So for example `challenges`
 * should be present in the model instead of adding it in all endpoint we can use this advice to let
 * Spring do this for us.
 */
@ControllerAdvice
public class AllControllerAdvice {

  private final Challenges challenges;
  private final String version;
  private final ScoreCard scoreCard;
  private final ChallengeDefinitionsConfiguration challengeDefinitionsConfiguration;
  private final RuntimeEnvironment runtimeEnvironment;

  public AllControllerAdvice(
      Challenges challenges,
      @Value("${APP_VERSION}") String version,
      ScoreCard scoreCard,
      ChallengeDefinitionsConfiguration challengeDefinitionsConfiguration,
      RuntimeEnvironment runtimeEnvironment) {
    this.challenges = challenges;
    this.version = version;
    this.scoreCard = scoreCard;
    this.challengeDefinitionsConfiguration = challengeDefinitionsConfiguration;
    this.runtimeEnvironment = runtimeEnvironment;
  }

  @ModelAttribute
  public void addChallenges(Model model) {
    model.addAttribute(
        "challenges",
        challengeDefinitionsConfiguration.challenges().stream()
            .map(
                def ->
                    ChallengeUI.toUI(
                        def,
                        scoreCard,
                        runtimeEnvironment,
                        challenges.difficulties(),
                        challenges.getDefinitions().environments(),
                        challenges.navigation(def)))
            .collect(Collectors.toList()));
  }

  @ModelAttribute
  public void addVersion(Model model) {
    model.addAttribute("version", version);
  }

  @ModelAttribute
  public void addRequest(Model model, HttpServletRequest request) {
    model.addAttribute("requestURI", request.getRequestURI());
  }

  @ModelAttribute
  public void addRuntimeEnvironment(Model model) {
    model.addAttribute("environment", runtimeEnvironment.getRuntimeEnvironment().displayName());
    model.addAttribute("ctf_enabled", runtimeEnvironment.runtimeInCTFMode());
  }
}
