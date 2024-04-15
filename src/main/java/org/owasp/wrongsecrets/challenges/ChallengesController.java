package org.owasp.wrongsecrets.challenges;

import static org.owasp.wrongsecrets.ChallengeConfigurationException.configError;

import com.google.common.base.Strings;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Supplier;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.owasp.wrongsecrets.ChallengeConfigurationException;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.docker.Challenge8;
import org.owasp.wrongsecrets.challenges.docker.authchallenge.Challenge37;
import org.owasp.wrongsecrets.challenges.docker.challenge30.Challenge30;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

/** Controller used to host the Challenges UI. */
@Controller
public class ChallengesController {

  private final ScoreCard scoreCard;
  private final RuntimeEnvironment runtimeEnvironment;
  private final Challenges challenges;

  @Value("${hints_enabled}")
  private boolean hintsEnabled;

  @Value("${reason_enabled}")
  private boolean reasonEnabled;

  @Value("${ctf_enabled}")
  private boolean ctfModeEnabled;

  private final boolean spoilingEnabled;

  @Value("${ctf_key}")
  private String ctfKey;

  @Value("${challenge_acht_ctf_to_provide_to_host_value}")
  private String keyToProvideToHost;

  @Value("${challenge_thirty_ctf_to_provide_to_host_value}")
  private String keyToProvideToHostForChallenge30;

  @Value("${challenge_rando_key_ctf_to_provide_to_host_value}")
  private String getKeyToProvideToHostChallenge37;

  @Value("${CTF_SERVER_ADDRESS}")
  private String ctfServerAddress;

  public ChallengesController(
      ScoreCard scoreCard,
      Challenges challenges,
      RuntimeEnvironment runtimeEnvironment,
      @Value("${spoiling_enabled}") boolean spoilingEnabled) {
    this.scoreCard = scoreCard;
    this.challenges = challenges;
    this.runtimeEnvironment = runtimeEnvironment;
    this.spoilingEnabled = spoilingEnabled;
  }

  /**
   * return a spoil of the secret Please note that there is no way to enable this in ctfMode: spoils
   * can never be returned during a CTF By default, in normal operations, spoils are enabled, unless
   * `spoilingEnabled` is set to false.
   *
   * @param model exchanged with the FE
   * @return either a notification or a spoil
   */
  @GetMapping("/spoil/{short-name}")
  @Hidden
  public String spoiler(@PathVariable("short-name") String shortName, Model model) {
    if (ctfModeEnabled) {
      model.addAttribute("spoiler", new Spoiler("Spoils are disabled in CTF mode"));
    } else if (!spoilingEnabled) {
      model.addAttribute("spoiler", new Spoiler("Spoils are disabled in the configuration"));
    } else {
      Optional<Spoiler> spoilerFromRuntimeEnvironment =
          challenges.findChallenge(shortName, runtimeEnvironment).map(Challenge::spoiler);
      Supplier<Spoiler> spoilerFromRandomChallenge =
          () -> {
            var challengeDefinition = findByShortName(shortName);
            return challenges.getChallenge(challengeDefinition).getFirst().spoiler();
          };

      // We always want to show the spoiler even if we run in a non-supported environment
      model.addAttribute(
          "spoiler", spoilerFromRuntimeEnvironment.orElseGet(spoilerFromRandomChallenge));
    }
    return "spoil";
  }

  private void addChallengeUI(Model model, ChallengeDefinition challengeDefinition) {
    model.addAttribute(
        "challenge",
        ChallengeUI.toUI(
            challengeDefinition,
            scoreCard,
            runtimeEnvironment,
            challenges.difficulties(),
            challenges.getDefinitions().environments(),
            challenges.navigation(challengeDefinition)));
  }

  /**
   * checks whether challenge is enabled based on used runtimemode and CTF enablement.
   *
   * @return boolean true if the challenge can run.
   */
  private boolean isChallengeEnabled(ChallengeDefinition challengeDefinition) {
    if (runtimeEnvironment.runtimeInCTFMode()) {
      return runtimeEnvironment.canRun(challengeDefinition) && challengeDefinition.ctf().enabled();
    }
    return runtimeEnvironment.canRun(challengeDefinition);
  }

  private ChallengeDefinition findByShortName(String shortName) {
    return challenges
        .findByShortName(shortName)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    configError("Challenge with short name '%s' not found", shortName).get()));
  }

  @GetMapping("/challenge/{short-name}")
  @Operation(description = "Returns the data for a given challenge's form interaction")
  public String challenge(Model model, @PathVariable("short-name") String shortName) {
    var challengeDefinition = findByShortName(shortName);
    model.addAttribute("challengeForm", new ChallengeForm(""));
    addChallengeUI(model, challengeDefinition);

    model.addAttribute("answerCorrect", null);
    model.addAttribute("answerIncorrect", null);
    model.addAttribute("solution", null);
    String clickNext =
        "This challenge has been disabled. Click \"next\" to go to the next challenge.";
    if (challenges.isLastChallenge(challengeDefinition)) {
      clickNext = "This challenge has been disabled";
    }
    if (!isChallengeEnabled(challengeDefinition)) {
      model.addAttribute("answerIncorrect", "This challenge has been disabled." + clickNext);
    }
    if (ctfModeEnabled && challenges.isFirstChallenge(challengeDefinition)) {
      if (!Strings.isNullOrEmpty(ctfServerAddress) && !ctfServerAddress.equals("not_set")) {
        model.addAttribute(
            "answerCorrect",
            "You are playing in CTF Mode where you need to give your answer once more to "
                + ctfServerAddress
                + " if it is correct. We have to do this as you can otherwise reverse engineer our"
                + " challenge flag generation process after completing the first 8 challenges");
      } else {
        model.addAttribute(
            "answerCorrect",
            "You are playing in CTF Mode, please submit the flag you receive after solving this"
                + " challenge to your CTFD/Facebook CTF instance");
      }
    }
    enrichWithHintsAndReasons(model);
    includeScoringStatus(model, challengeDefinition);
    addWarning(challengeDefinition, model);
    fireEnding(model);
    return "challenge";
  }

  @PostMapping(value = "/challenge/{name}", params = "action=reset")
  @Operation(description = "Resets the state of a given challenge")
  public String reset(
      @ModelAttribute ChallengeForm challengeForm, @PathVariable String name, Model model) {
    var challengeDefinition = findByShortName(name);
    scoreCard.reset(challengeDefinition);

    addChallengeUI(model, challengeDefinition);
    includeScoringStatus(model, challengeDefinition);
    addWarning(challengeDefinition, model);
    enrichWithHintsAndReasons(model);
    return "challenge";
  }

  @PostMapping(value = "/challenge/{name}", params = "action=submit")
  @Operation(description = "Post your answer to the challenge for a given challenge")
  public String postController(
      @ModelAttribute ChallengeForm challengeForm, Model model, @PathVariable String name) {
    var challengeDefinition = findByShortName(name);

    if (!isChallengeEnabled(challengeDefinition)) {
      model.addAttribute("answerIncorrect", "This challenge has been disabled.");
    } else {
      var challenge =
          challenges
              .findChallenge(name, runtimeEnvironment)
              .orElseThrow(
                  () ->
                      new ChallengeConfigurationException(
                          configError(
                              "Challenge '%s' not found for environment: '%s'",
                              name, runtimeEnvironment.getRuntimeEnvironment().name())));

      if (challenge.answerCorrect(challengeForm.solution())) {
        scoreCard.completeChallenge(challengeDefinition);
        // TODO extract this to a separate method probably have separate handler classes in the
        // configuration otherwise this is not maintainable, probably give the challenge a CTF
        // method hook which you can override and do these kind of things in there.
        if (ctfModeEnabled) {
          if (!Strings.isNullOrEmpty(ctfServerAddress) && !ctfServerAddress.equals("not_set")) {
            if (challenge instanceof Challenge8) {
              if (!Strings.isNullOrEmpty(keyToProvideToHost)
                  && !keyToProvideToHost.equals(
                      "not_set")) { // this means that it was overriden with a code that needs to be
                // returned to the ctf key exchange host.
                model.addAttribute(
                    "answerCorrect",
                    "Your answer is correct! "
                        + "fill in the following answer in the CTF instance at "
                        + ctfServerAddress
                        + "for which you get your code: "
                        + keyToProvideToHost);
              }
            } else if (challenge instanceof Challenge30) {
              if (!Strings.isNullOrEmpty(keyToProvideToHostForChallenge30)
                  && !keyToProvideToHostForChallenge30.equals(
                      "not_set")) { // this means that it was overriden with a code that needs to be
                // returned to the ctf key exchange host.
                model.addAttribute(
                    "answerCorrect",
                    "Your answer is correct! "
                        + "fill in the following answer in the CTF instance at "
                        + ctfServerAddress
                        + "for which you get your code: "
                        + keyToProvideToHostForChallenge30);
              }
            } else if (challenge instanceof Challenge37) {
              if (!Strings.isNullOrEmpty(getKeyToProvideToHostChallenge37)
                  && !keyToProvideToHostForChallenge30.equals(
                      "not_set")) { // this means that it was overriden with a code that needs to be
                // returned to the ctf key exchange hos
                model.addAttribute(
                    "answerCorrect",
                    "Your answer is correct! "
                        + "fill in the following answer in the CTF instance at "
                        + ctfServerAddress
                        + "for which you get your code: "
                        + getKeyToProvideToHostChallenge37);
              }
            } else {
              model.addAttribute(
                  "answerCorrect",
                  "Your answer is correct! "
                      + "fill in the same answer in the ctf-instance of the app: "
                      + ctfServerAddress);
            }
          } else {
            String code = generateCode(challengeDefinition);
            model.addAttribute(
                "answerCorrect",
                "Your answer is correct! " + "fill in the following code in CTF scoring: " + code);
          }
        } else {
          model.addAttribute("answerCorrect", "Your answer is correct!");
        }
      } else {
        model.addAttribute("answerIncorrect", "Your answer is incorrect, try harder ;-)");
      }
    }
    addChallengeUI(model, challengeDefinition);
    includeScoringStatus(model, challengeDefinition);
    enrichWithHintsAndReasons(model);

    fireEnding(model);
    return "challenge";
  }

  // TODO extract this to the challenge definition @see ChallengeAPIController with nested if
  // statement
  private String generateCode(ChallengeDefinition challenge) {
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(ctfKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
    try {
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(secretKeySpec);
      byte[] result = mac.doFinal(challenge.name().name().getBytes(StandardCharsets.UTF_8));
      return new String(Hex.encode(result));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  private void includeScoringStatus(Model model, ChallengeDefinition challenge) {
    model.addAttribute("totalPoints", scoreCard.getTotalReceivedPoints());
    model.addAttribute("progress", "" + scoreCard.getProgress());

    if (scoreCard.getChallengeCompleted(challenge)) {
      model.addAttribute("challengeCompletedAlready", "This exercise is already completed");
    }
  }

  private void addWarning(ChallengeDefinition challenge, Model model) {
    if (!runtimeEnvironment.canRun(challenge)) {
      if (challenge.missingEnvironment() != null) {
        model.addAttribute("missingEnvWarning", challenge.missingEnvironment().contents().get());
      } else {
        var warning =
            challenge.supportedEnvironments().stream()
                .limit(1)
                .map(env -> env.missingEnvironment().contents().get())
                .findFirst()
                .orElse(null);
        model.addAttribute("missingEnvWarning", warning);
      }
    }
  }

  private void enrichWithHintsAndReasons(Model model) {
    model.addAttribute("hintsEnabled", hintsEnabled);
    model.addAttribute("reasonEnabled", reasonEnabled);
  }

  private void fireEnding(Model model) {
    var notCompleted =
        challenges.getDefinitions().challenges().stream()
            .filter(this::isChallengeEnabled)
            .filter(this::challengeNotCompleted)
            .count();
    if (notCompleted == 0) {
      model.addAttribute("allCompleted", "party");
    }
  }

  private boolean challengeNotCompleted(ChallengeDefinition challenge) {
    return !scoreCard.getChallengeCompleted(challenge);
  }
}
