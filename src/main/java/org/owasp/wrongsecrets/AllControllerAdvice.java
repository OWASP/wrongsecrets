package org.owasp.wrongsecrets;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

  private final List<ChallengeUI> challenges;
  private final String version;
  private final RuntimeEnvironment runtimeEnvironment;

  public AllControllerAdvice(
      List<Challenge> challenges,
      @Value("${APP_VERSION}") String version,
      RuntimeEnvironment runtimeEnvironment) {
    this.challenges = ChallengeUI.toUI(challenges, runtimeEnvironment);
    this.version = version;
    this.runtimeEnvironment = runtimeEnvironment;
  }

  @ModelAttribute
  public void addChallenges(Model model) {
    model.addAttribute("challenges", challenges);
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
    model.addAttribute("environment", runtimeEnvironment.getRuntimeEnvironment().name());
    model.addAttribute("ctf_enabled", runtimeEnvironment.runtimeInCTFMode());
  }

  @Bean
  public List<ChallengeUI> uiChallenges() {
    return challenges;
  }
}
