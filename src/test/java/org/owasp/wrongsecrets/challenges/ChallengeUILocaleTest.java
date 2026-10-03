package org.owasp.wrongsecrets.challenges;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

@SpringBootTest(
    properties = {"K8S_ENV=DOCKER"},
    classes = WrongSecretsApplication.class)
class ChallengeUILocaleTest {

  private static final List<String> SUPPORTED_LANGUAGES = List.of("nl", "de", "es", "fr", "uk");

  @Autowired private ChallengeDefinitionsConfiguration definitions;
  @Autowired private Challenges challenges;
  @Autowired private ScoreCard scoreCard;
  @Autowired private RuntimeEnvironment runtimeEnvironment;

  @AfterEach
  void resetLocaleContext() {
    LocaleContextHolder.resetLocaleContext();
  }

  private ChallengeUI openingChallenge() {
    ChallengeDefinition definition = definitions.challenges().getFirst();
    return ChallengeUI.toUI(
        definition,
        scoreCard,
        runtimeEnvironment,
        challenges.difficulties(),
        challenges.getDefinitions().environments(),
        challenges.navigation(definition));
  }

  @Test
  void englishLocaleKeepsDefaultFileNames() {
    LocaleContextHolder.setLocale(Locale.ENGLISH);
    var challenge = openingChallenge();

    assertThat(challenge.getExplanation()).isEqualTo("explanations/challenge0.adoc");
    assertThat(challenge.getHint()).isEqualTo("explanations/challenge0_hint.adoc");
    assertThat(challenge.getReason()).isEqualTo("explanations/challenge0_reason.adoc");
  }

  @Test
  void supportedLocalesResolveLocalizedFileNames() {
    for (String language : SUPPORTED_LANGUAGES) {
      LocaleContextHolder.setLocale(Locale.of(language));
      var challenge = openingChallenge();

      assertThat(challenge.getExplanation())
          .as("explanation for %s", language)
          .isEqualTo("explanations/challenge0_" + language + ".adoc");
      assertThat(challenge.getHint())
          .as("hint for %s", language)
          .isEqualTo("explanations/challenge0_hint_" + language + ".adoc");
      assertThat(challenge.getReason())
          .as("reason for %s", language)
          .isEqualTo("explanations/challenge0_reason_" + language + ".adoc");
    }
  }

  @Test
  void regionalLocaleUsesLanguageSpecificFile() {
    LocaleContextHolder.setLocale(Locale.of("de", "DE"));
    var challenge = openingChallenge();

    assertThat(challenge.getExplanation()).isEqualTo("explanations/challenge0_de.adoc");
    assertThat(challenge.getHint()).isEqualTo("explanations/challenge0_hint_de.adoc");
    assertThat(challenge.getReason()).isEqualTo("explanations/challenge0_reason_de.adoc");
  }

  @Test
  void unsupportedLocaleFallsBackToEnglishFiles() {
    LocaleContextHolder.setLocale(Locale.CHINESE);
    var challenge = openingChallenge();

    assertThat(challenge.getExplanation()).isEqualTo("explanations/challenge0.adoc");
    assertThat(challenge.getHint()).isEqualTo("explanations/challenge0_hint.adoc");
    assertThat(challenge.getReason()).isEqualTo("explanations/challenge0_reason.adoc");
  }
}
