package org.owasp.wrongsecrets.challenges;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

/** Tests that {@link ChallengeUI} resolves locale-specific content files with English fallback. */
@SpringBootTest(properties = {"K8S_ENV=DOCKER"})
class ChallengeUILocaleTest {

  @Autowired private ChallengeDefinitionsConfiguration challengeDefinitionsConfiguration;
  @Autowired private ScoreCard scoreCard;
  @Autowired private RuntimeEnvironment runtimeEnvironment;
  @Autowired private Challenges challenges;

  @AfterEach
  void resetLocale() {
    LocaleContextHolder.resetLocaleContext();
  }

  private ChallengeUI challengeUIFor(int index) {
    var def = challengeDefinitionsConfiguration.challenges().get(index);
    return ChallengeUI.toUI(
        def,
        scoreCard,
        runtimeEnvironment,
        challenges.difficulties(),
        challenges.getDefinitions().environments(),
        challenges.navigation(def));
  }

  @Test
  void englishLocaleReturnsDefaultExplanation() {
    LocaleContextHolder.setLocale(Locale.ENGLISH);
    var ui = challengeUIFor(0);
    assertThat(ui.getExplanation()).isEqualTo("explanations/challenge0.adoc");
  }

  @Test
  void germanLocaleReturnsTranslatedExplanationWhenFileExists() {
    LocaleContextHolder.setLocale(Locale.GERMAN);
    var ui = challengeUIFor(0); // challenge0_de.adoc exists
    assertThat(ui.getExplanation()).isEqualTo("explanations/challenge0_de.adoc");
  }

  @Test
  void dutchLocaleReturnsTranslatedHintWhenFileExists() {
    LocaleContextHolder.setLocale(Locale.forLanguageTag("nl"));
    var ui = challengeUIFor(0); // challenge0_hint_nl.adoc exists
    assertThat(ui.getHint()).isEqualTo("explanations/challenge0_hint_nl.adoc");
  }

  @Test
  void frenchLocaleReturnsFallbackForUntranslatedChallenge() {
    LocaleContextHolder.setLocale(Locale.FRENCH);
    var ui = challengeUIFor(3); // challenge3 has no French translation
    assertThat(ui.getExplanation()).isEqualTo("explanations/challenge3.adoc");
  }

  @Test
  void spanishLocaleReturnsTranslatedReasonWhenFileExists() {
    LocaleContextHolder.setLocale(Locale.forLanguageTag("es"));
    var ui = challengeUIFor(1); // challenge1_reason_es.adoc exists
    assertThat(ui.getReason()).isEqualTo("explanations/challenge1_reason_es.adoc");
  }
}
