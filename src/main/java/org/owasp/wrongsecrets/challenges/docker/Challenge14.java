package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.linguafranca.pwdb.Database;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry;
import org.linguafranca.pwdb.kdbx.simple.SimpleGroup;
import org.linguafranca.pwdb.kdbx.simple.SimpleIcon;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a weak password for your password manager. */
@Slf4j
@Component
public class Challenge14 extends FixedAnswerChallenge {

  private final String keepassxPassword;
  private final String defaultKeepassValue;
  private final String filePath;

  public Challenge14(
      @Value("${keepasxpassword}") String keepassxPassword,
      @Value("${KEEPASS_BROKEN}") String defaultKeepassValue,
      @Value("${keepasspath}") String filePath) {
    this.keepassxPassword = keepassxPassword;
    this.defaultKeepassValue = defaultKeepassValue;
    this.filePath = filePath;
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN")
  public String getAnswer() {
    if (Strings.isNullOrEmpty(keepassxPassword)) {
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
}
