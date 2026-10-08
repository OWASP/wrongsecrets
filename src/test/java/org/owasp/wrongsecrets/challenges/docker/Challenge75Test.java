package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.owasp.wrongsecrets.Challenges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@SpringBootTest
class Challenge75Test {

  private static final String SETTINGS_LOCATION = "challenges/challenge-75/settings.xml";

  private static final String DECOY_SERVER =
      """
      <server>
        <id>central</id>
        <username>deployment</username>
        <password>DecoyRepositoryPassword456</password>
      </server>
      """;

  @Autowired private Challenges challenges;

  private static Path writeSettings(Path tempDir, String content) throws IOException {
    var settingsFile = tempDir.resolve("settings.xml");
    Files.writeString(settingsFile, content);
    return settingsFile;
  }

  private static String settingsXml(String servers) {
    return """
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
      <servers>
    %s
      </servers>
    </settings>
    """
        .formatted(servers);
  }

  private static String nexusServer(String password) {
    return """
    <server>
      <id>%s</id>
      <username>wrongsecrets-deployer</username>
      <password>%s</password>
    </server>
    """
        .formatted(Challenge75.NEXUS_SERVER_ID, password);
  }

  @Test
  void shippedSettingsXmlShouldExistAndDeclareTheNexusServer() throws IOException {
    var resource = new ClassPathResource(SETTINGS_LOCATION);
    var content = resource.getContentAsString(StandardCharsets.UTF_8);

    assertThat(content).contains("<id>" + Challenge75.NEXUS_SERVER_ID + "</id>");
  }

  @Test
  void spoilerShouldGiveThePasswordOfTheShippedSettingsXml() throws IOException {
    var challenge = new Challenge75(new ClassPathResource(SETTINGS_LOCATION));
    var resource = new ClassPathResource(SETTINGS_LOCATION);
    var content = resource.getContentAsString(StandardCharsets.UTF_8);

    assertThat(challenge.spoiler().solution()).isNotEmpty().isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(content).contains(challenge.spoiler().solution());
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void shouldExtractThePasswordFromTheSettingsXml(@TempDir Path tempDir) throws IOException {
    var settingsFile = writeSettings(tempDir, settingsXml(nexusServer("FixtureNexusPassword123")));
    var challenge = new Challenge75(new FileSystemResource(settingsFile));

    assertThat(challenge.spoiler().solution()).isEqualTo("FixtureNexusPassword123");
    assertThat(challenge.answerCorrect("FixtureNexusPassword123")).isTrue();
  }

  @Test
  void shouldUseTheNexusServerAndIgnoreOtherServers(@TempDir Path tempDir) throws IOException {
    var settingsFile =
        writeSettings(tempDir, settingsXml(DECOY_SERVER + nexusServer("FixtureNexusPassword789")));
    var challenge = new Challenge75(new FileSystemResource(settingsFile));

    assertThat(challenge.spoiler().solution()).isEqualTo("FixtureNexusPassword789");
    assertThat(challenge.answerCorrect("FixtureNexusPassword789")).isTrue();
    assertThat(challenge.answerCorrect("DecoyRepositoryPassword456")).isFalse();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge(@TempDir Path tempDir) throws IOException {
    var settingsFile = writeSettings(tempDir, settingsXml(nexusServer("FixtureNexusPassword123")));
    var challenge = new Challenge75(new FileSystemResource(settingsFile));

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void missingSettingsFileShouldReportAnError() {
    var challenge =
        new Challenge75(new ClassPathResource("challenges/challenge-75/does-not-exist.xml"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void malformedSettingsXmlShouldReportAnError(@TempDir Path tempDir) throws IOException {
    var settingsFile = writeSettings(tempDir, "<settings><servers><server><id>unfinished");

    var challenge = new Challenge75(new FileSystemResource(settingsFile));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void settingsXmlWithoutTheNexusServerShouldReportAnError(@TempDir Path tempDir)
      throws IOException {
    var settingsFile = writeSettings(tempDir, settingsXml(DECOY_SERVER));

    var challenge = new Challenge75(new FileSystemResource(settingsFile));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void doctypeDeclarationsShouldBeRejected(@TempDir Path tempDir) throws IOException {
    var settingsFile =
        writeSettings(
            tempDir,
            """
            <?xml version="1.0"?>
            <!DOCTYPE settings [
              <!ENTITY xxe SYSTEM "file:///etc/passwd">
            ]>
            <settings>
              <servers>
                <server>
                  <id>wrongsecrets-nexus</id>
                  <password>&xxe;</password>
                </server>
              </servers>
            </settings>
            """);

    var challenge = new Challenge75(new FileSystemResource(settingsFile));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void challenge75ShouldBeRegisteredAndSolveable() {
    var definition = challenges.findByShortName("challenge-75");

    assertThat(definition).isPresent();
    assertThat(challenges.getChallenge(definition.get())).hasSize(1);
    var challenge = challenges.getChallenge(definition.get()).getFirst();

    assertThat(challenge).isInstanceOf(Challenge75.class);
    assertThat(challenge.spoiler().solution()).isNotEmpty().isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
