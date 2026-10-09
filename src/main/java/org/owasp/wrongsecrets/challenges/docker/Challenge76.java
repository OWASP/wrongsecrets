package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Challenge about intentionally vulnerable password storage in a shipped SQLite user database. The
 * database holds ~2000 synthetic user records whose passwords are stored twice: as an unsalted MD5
 * hash and as AES-128-ECB ciphertext produced with a key that ships together with this source code.
 * At runtime one database user is selected as the target: its username is displayed in the
 * explanation and the recovered password of that exact user is the answer of this challenge.
 */
@Slf4j
@Component
public class Challenge76 extends FixedAnswerChallenge {

  static final String ENCRYPTION_KEY = "SqliteWeakKey76!";

  private static final String TARGET_USER_QUERY =
      "SELECT username, password_encrypted FROM users ORDER BY RANDOM() LIMIT 1";

  private final Resource databaseFile;

  private volatile DatabaseUser targetUser;

  /**
   * Constructor for creating a new Challenge76 object.
   *
   * @param databaseFile the SQLite database containing the synthetic user records.
   */
  public Challenge76(
      @Value("classpath:challenges/challenge-76/wrongsecrets-users.sqlite") Resource databaseFile) {
    this.databaseFile = databaseFile;
  }

  /**
   * Gives the username of the user that is dynamically selected as the target of this challenge,
   * this is the username that gets displayed in the explanation.
   *
   * @return the username of the selected target user.
   */
  public String getTargetUsername() {
    return targetUser().username();
  }

  @Override
  public String getAnswer() {
    return targetUser().password();
  }

  private DatabaseUser targetUser() {
    DatabaseUser user = targetUser;
    if (user == null) {
      synchronized (this) {
        if (targetUser == null) {
          targetUser = loadTargetUser();
        }
        user = targetUser;
      }
    }
    return user;
  }

  private DatabaseUser loadTargetUser() {
    Path temporaryDatabase = null;
    try {
      temporaryDatabase = Files.createTempFile("wrongsecrets-challenge76", ".sqlite");
      try (var inputStream = databaseFile.getInputStream()) {
        Files.copy(inputStream, temporaryDatabase, StandardCopyOption.REPLACE_EXISTING);
      }
      try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + temporaryDatabase);
          var statement = connection.createStatement();
          var resultSet = statement.executeQuery(TARGET_USER_QUERY)) {
        if (resultSet.next()) {
          return new DatabaseUser(
              resultSet.getString("username"), decrypt(resultSet.getString("password_encrypted")));
        }
        log.warn("No users found in the user database of challenge 76");
      }
    } catch (IOException | SQLException e) {
      log.warn("Could not read the user database of challenge 76", e);
    } catch (GeneralSecurityException | IllegalArgumentException e) {
      log.warn("Could not decrypt the password of the target user of challenge 76", e);
      return DatabaseUser.error(DECRYPTION_ERROR);
    } finally {
      deleteTemporaryDatabase(temporaryDatabase);
    }
    return DatabaseUser.error(FILE_MOUNT_ERROR);
  }

  @SuppressFBWarnings(
      value = {"CIPHER_INTEGRITY", "ECB_MODE"},
      justification = "Intentionally weak ECB mode so every stored password stays recoverable")
  private String decrypt(String cipherText) throws GeneralSecurityException {
    SecretKey secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
    // codeql[java/weak-cryptographic-algorithm] Intentionally weak ECB mode for educational
    // challenge about vulnerable password storage
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(cipherText));
    return new String(decryptedData, StandardCharsets.UTF_8);
  }

  private void deleteTemporaryDatabase(Path temporaryDatabase) {
    if (temporaryDatabase == null) {
      return;
    }
    try {
      Files.deleteIfExists(temporaryDatabase);
    } catch (IOException e) {
      log.debug("Could not delete the temporary database copy of challenge 76", e);
    }
  }

  private record DatabaseUser(String username, String password) {

    private static DatabaseUser error(String password) {
      return new DatabaseUser("unknown", password);
    }
  }
}
