package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Challenge demonstrating how an MCP server with an overly-privileged Google Service Account can be
 * used to escalate privileges and access Google Drive documents that the caller is not authorized
 * to read directly.
 */
@Component
@Slf4j
public class Challenge62 implements Challenge {

  private static final String SECRET_START_TAG = "<secret>";
  private static final String SECRET_END_TAG = "</secret>";
  private static final String DEFAULT_PLACEHOLDER =
      "if_you_see_this_configure_the_google_service_account_properly";
  private static final String DEFAULT_DOCUMENT_ID = "1PlZkwEd7GouyY4cdOxBuczm6XumQeuZN31LR2BXRgPs";

  private final String configuredGoogleDriveSecret;
  private final String documentId;
  private final Challenge62McpController challenge62McpController;
  // Holds the normalized answer value used by spoiler()/answerCorrect().
  // Normalization extracts the value between <secret> and </secret> once at load time.
  private String cachedResolvedSecret = "";
  private boolean cachedResolvedSecretLoaded;

  @Autowired
  public Challenge62(
      @Value(
              "${WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET:if_you_see_this_configure_the_google_service_account_properly}")
          String configuredGoogleDriveSecret,
      @Value("${GOOGLE_DRIVE_DOCUMENT_ID:1PlZkwEd7GouyY4cdOxBuczm6XumQeuZN31LR2BXRgPs}")
          String documentId,
      Challenge62McpController challenge62McpController) {
    this.configuredGoogleDriveSecret = configuredGoogleDriveSecret;
    this.documentId = documentId;
    this.challenge62McpController = challenge62McpController;
  }

  Challenge62(String configuredGoogleDriveSecret) {
    this(configuredGoogleDriveSecret, DEFAULT_DOCUMENT_ID, null);
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(resolveSecret());
  }

  @Override
  public boolean answerCorrect(String answer) {
    if (Strings.isNullOrEmpty(answer)) {
      return false;
    }

    String resolvedSecret = resolveSecret();
    return !Strings.isNullOrEmpty(resolvedSecret) && resolvedSecret.equals(answer.trim());
  }

  /**
   * Extracts the value between {@code <secret>} and {@code </secret>}.
   *
   * <p>Returns {@code null} when tags are missing or malformed.
   */
  private String extractSecretValue(String resolvedSecret) {
    if (Strings.isNullOrEmpty(resolvedSecret)) {
      return null;
    }

    int secretStart = resolvedSecret.indexOf(SECRET_START_TAG);
    if (secretStart < 0) {
      return null;
    }

    int valueStart = secretStart + SECRET_START_TAG.length();
    int secretEnd = resolvedSecret.indexOf(SECRET_END_TAG, valueStart);
    if (secretEnd < 0) {
      return null;
    }

    return resolvedSecret.substring(valueStart, secretEnd).trim();
  }

  /**
   * Resolves and caches the challenge answer once.
   *
   * <p>If {@code WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET} is explicitly configured, that value is used
   * as source content and normalized to the tagged secret. Otherwise, the value is loaded from the
   * configured Google Drive document through {@link Challenge62McpController} and then normalized.
   */
  private String resolveSecret() {
    if (cachedResolvedSecretLoaded) {
      return cachedResolvedSecret;
    }

    if (!DEFAULT_PLACEHOLDER.equals(configuredGoogleDriveSecret)) {
      cachedResolvedSecret = Strings.nullToEmpty(extractSecretValue(configuredGoogleDriveSecret));
      cachedResolvedSecretLoaded = true;
      return cachedResolvedSecret;
    }

    if (challenge62McpController == null) {
      return configuredGoogleDriveSecret;
    }

    try {
      log.info("Attempting to read secret from Google Drive document with ID: {}", documentId);
      String loadedDocument = challenge62McpController.readGoogleDriveDocument(documentId);
      cachedResolvedSecret = Strings.nullToEmpty(extractSecretValue(loadedDocument));
      cachedResolvedSecretLoaded = true;
      return Strings.isNullOrEmpty(cachedResolvedSecret)
          ? configuredGoogleDriveSecret
          : cachedResolvedSecret;
    } catch (Exception ignored) {
      return configuredGoogleDriveSecret;
    }
  }
}
