package org.owasp.wrongsecrets;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.owasp.wrongsecrets.challenges.ChallengeUI;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AllControllerAdvice {

  private final Challenges challenges;
  private final String version;
  private final ScoreCard scoreCard;
  private final ChallengeDefinitionsConfiguration challengeDefinitionsConfiguration;
  private final RuntimeEnvironment runtimeEnvironment;

  private List<ChallengeUI> cachedChallenges;

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

  @PostConstruct
  public void init() {
    this.cachedChallenges =
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
            .collect(Collectors.toList());
  }

  @ModelAttribute
  public void addChallenges(Model model) {
    model.addAttribute("challenges", cachedChallenges);
  }

  @ModelAttribute
  public void addVersion(Model model) {
    model.addAttribute("version", version);
  }

  @ModelAttribute
  public void addRequest(Model model, HttpServletRequest request) {
    model.addAttribute("requestURI", request.getRequestURI());
    model.addAttribute("httpServletRequest", request);
  }

  @ModelAttribute
  public void addRuntimeEnvironment(Model model) {
    model.addAttribute("environment", runtimeEnvironment.getRuntimeEnvironment().displayName());
    model.addAttribute("ctf_enabled", runtimeEnvironment.runtimeInCTFMode());
  }
}
