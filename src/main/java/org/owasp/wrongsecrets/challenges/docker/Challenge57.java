package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** Challenge demonstrating database connection string exposure through error messages. */
@Slf4j
@Component
public class Challenge57 implements Challenge {

  // Simulated database connection string with embedded credentials
  private static final String DB_CONNECTION_STRING = 
      "jdbc:postgresql://db.example.com:5432/userdb?user=dbadmin&password=SuperSecretDB2024!&ssl=true";
  
  private static final String EXPECTED_SECRET = "SuperSecretDB2024!";

  @Override
  public Spoiler spoiler() {
    return new Spoiler(EXPECTED_SECRET);
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer) && EXPECTED_SECRET.equals(answer.trim());
  }

  /**
   * This method simulates what happens when an application tries to connect to a database
   * but fails, exposing the full connection string (including credentials) in error messages.
   * This is a common real-world mistake where developers include sensitive information
   * in connection strings and don't properly handle/sanitize database connection errors.
   */
  public String simulateDatabaseConnectionError() {
    try {
      // This will fail since we don't have a real database, but it demonstrates 
      // how connection errors can expose credentials
      Connection conn = DriverManager.getConnection(DB_CONNECTION_STRING);
      conn.close();
      return "Connection successful";
    } catch (SQLException e) {
      // Poor error handling - exposing the full connection string in the error message
      String errorMessage = "Database connection failed with connection string: " + DB_CONNECTION_STRING 
          + "\nError: " + e.getMessage();
      
      // Log the error (another way credentials get exposed)
      log.error("Failed to connect to database: {}", errorMessage);
      
      return errorMessage;
    }
  }
}