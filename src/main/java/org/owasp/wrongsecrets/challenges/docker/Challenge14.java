package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry;
import org.linguafranca.pwdb.kdbx.simple.SimpleGroup;
import org.linguafranca.pwdb.kdbx.simple.SimpleIcon;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about having a weak password for your password manager. */
@Slf4j
@Component
@Order(14)
public class Challenge14 extends Challenge {

  private final String keepassxPassword;
  private final String defaultKeepassValue;
  private final String filePath;

  public Challenge14(
      ScoreCard scoreCard,
      @Value("${keepasxpassword}") String keepassxPassword,
      @Value("${KEEPASS_BROKEN}") String defaultKeepassValue,
      @Value("${keepasspath}") String filePath) {
    super(scoreCard);
    this.keepassxPassword = keepassxPassword;
    this.defaultKeepassValue = defaultKeepassValue;
    this.filePath = filePath;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(findAnswer());
  }

  /** {@inheritDoc} */
  @Override
  protected boolean answerCorrect(String answer) {
    return isanswerCorrectInKeeyPassx(answer);
  }

  /** {@inheritDoc} */
  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EXPERT;
  }

  /** {@inheritDoc} Password manager based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.PASSWORD_MANAGER.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN")
  private String findAnswer() {
    if (Strings.isNullOrEmpty(keepassxPassword)) {
      // log.debug("Checking secret with values {}", keepassxPassword);
      return defaultKeepassValue;
    }
    KdbxCreds creds = new KdbxCreds(keepassxPassword.getBytes(StandardCharsets.UTF_8));
    Database<SimpleDatabase, SimpleGroup, SimpleEntry, SimpleIcon> database;

    try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
      database = SimpleDatabase.load(creds, inputStream);
      return database.findEntries("alibaba").get(0).getPassword();
    } catch (Exception | Error e) {
      log.error("Exception or Error with Challenge 14", e);
      try (InputStream inputStream =
          Files.newInputStream(Paths.get("src/test/resources/alibabacreds.kdbx"))) {
        database = SimpleDatabase.load(creds, inputStream);
        return database.findEntries("alibaba").get(0).getPassword();
      } catch (Exception | Error e2) {
        log.error("Exception or Error with Challenge 14 second time", e2);
        return defaultKeepassValue;
      }
    }
  }

  private boolean isanswerCorrectInKeeyPassx(String answer) {
    if (Strings.isNullOrEmpty(keepassxPassword) || Strings.isNullOrEmpty(answer)) {
      // log.debug("Checking secret with values {}, {}", keepassxPassword, answer);
      return false;
    }
    return answer.equals(findAnswer());
  }
}
