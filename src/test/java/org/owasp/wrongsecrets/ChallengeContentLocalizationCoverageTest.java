package org.owasp.wrongsecrets;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.owasp.wrongsecrets.definitions.Sources.ChallengeSource;
import org.owasp.wrongsecrets.definitions.Sources.TextWithFileLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

/**
 * Verifies that every challenge content file referenced by the configuration (explanation, hint and
 * reason of all 77 challenges, including cloud and limited-hint variants) exists in English, and
 * that the content of challenge 76 is translated for each supported locale (nl, de, es, fr, uk).
 */
@SpringBootTest
class ChallengeContentLocalizationCoverageTest {

  private static final List<String> SUPPORTED_LANGUAGES = List.of("nl", "de", "es", "fr", "uk");

  private static final List<String> CHALLENGE_76_CONTENT_FILES =
      List.of(
          "explanations/challenge76.adoc",
          "explanations/challenge76_hint.adoc",
          "explanations/challenge76_reason.adoc");

  @Autowired private ChallengeDefinitionsConfiguration definitions;

  @Test
  void everyReferencedChallengeContentFileExistsInEnglish() {
    Set<String> referencedFiles = new HashSet<>();
    for (var definition : definitions.challenges()) {
      for (ChallengeSource source : definition.sources()) {
        Stream.of(source.explanation(), source.hint(), source.reason(), source.hintLimited())
            .filter(Objects::nonNull)
            .map(TextWithFileLocation::fileName)
            .filter(Objects::nonNull)
            .filter(fileName -> !fileName.isBlank())
            .forEach(referencedFiles::add);
      }
    }

    assertThat(definitions.challenges()).hasSize(77);
    assertThat(referencedFiles).hasSize(254);

    for (String fileName : referencedFiles) {
      assertThat(new ClassPathResource(fileName).exists())
          .as("English content file %s should exist", fileName)
          .isTrue();
    }
  }

  @Test
  void challenge76ContentShouldBeTranslatedForAllSupportedLocales() {
    for (String fileName : CHALLENGE_76_CONTENT_FILES) {
      assertThat(new ClassPathResource(fileName).exists())
          .as("English content file %s should exist", fileName)
          .isTrue();

      String base = fileName.substring(0, fileName.lastIndexOf('.'));
      for (String language : SUPPORTED_LANGUAGES) {
        String localizedFileName = base + "_" + language + ".adoc";
        assertThat(new ClassPathResource(localizedFileName).exists())
            .as("Localized file %s for language %s should exist", fileName, language)
            .isTrue();
      }
    }
  }
}
