package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.DriverManager;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class Challenge76Test {

  private static final String DATABASE_LOCATION =
      "challenges/challenge-76/wrongsecrets-users.sqlite";

  @Test
  void spoilerShouldGiveThePasswordOfTheSelectedTargetUser() {
    var challenge = new Challenge76(new ClassPathResource(DATABASE_LOCATION));

    assertThat(challenge.spoiler().solution())
        .isNotEmpty()
        .isNotEqualTo(DECRYPTION_ERROR)
        .isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    assertThat(challenge.answerCorrect("not-the-password-of-the-target-user")).isFalse();
  }

  @Test
  void displayedUsernameAndAnswerShouldStayConsistent() {
    var challenge = new Challenge76(new ClassPathResource(DATABASE_LOCATION));

    var targetUsername = challenge.getTargetUsername();

    assertThat(targetUsername).isNotBlank();
    assertThat(challenge.getTargetUsername()).isEqualTo(targetUsername);
    assertThat(challenge.getAnswer()).isEqualTo(challenge.spoiler().solution());
  }

  @Test
  void targetUsernameShouldExistInTheShippedDatabase() throws Exception {
    var challenge = new Challenge76(new ClassPathResource(DATABASE_LOCATION));
    var targetUsername = challenge.getTargetUsername();

    try (var connection = DriverManager.getConnection(databaseUrl());
        var statement =
            connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
      statement.setString(1, targetUsername);
      try (var resultSet = statement.executeQuery()) {
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getInt(1)).isEqualTo(1);
      }
    }
  }

  @Test
  void everyUserInTheShippedDatabaseShouldBeRecoverableOffline() throws Exception {
    var userCount = 0;

    try (var connection = DriverManager.getConnection(databaseUrl());
        var statement = connection.createStatement();
        var resultSet =
            statement.executeQuery(
                "SELECT username, password_hash, password_encrypted FROM users")) {
      while (resultSet.next()) {
        var password = decrypt(resultSet.getString("password_encrypted"));
        var md5 = MessageDigest.getInstance("MD5");
        var hash = HexFormat.of().formatHex(md5.digest(password.getBytes(StandardCharsets.UTF_8)));

        assertThat(resultSet.getString("username")).isNotBlank();
        assertThat(password).isNotBlank();
        assertThat(hash).isEqualTo(resultSet.getString("password_hash"));
        userCount++;
      }
    }

    assertThat(userCount).isBetween(1900, 2100);
  }

  @Test
  void shouldReportAnErrorWhenTheDatabaseIsMissing() {
    var challenge =
        new Challenge76(new ClassPathResource("challenges/challenge-76/does-not-exist.sqlite"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(FILE_MOUNT_ERROR)).isTrue();
  }

  private String databaseUrl() throws Exception {
    var databasePath = Path.of(new ClassPathResource(DATABASE_LOCATION).getURL().toURI());
    return "jdbc:sqlite:" + databasePath;
  }

  private String decrypt(String cipherText) throws Exception {
    var cipher = Cipher.getInstance("AES");
    cipher.init(
        Cipher.DECRYPT_MODE,
        new SecretKeySpec(Challenge76.ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
    return new String(
        cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
  }
}
