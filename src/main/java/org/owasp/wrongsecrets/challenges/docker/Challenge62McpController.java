package org.owasp.wrongsecrets.challenges.docker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * MCP (Model Context Protocol) server endpoint for Challenge 62. Demonstrates how an MCP server
 * configured with an overly-privileged Google Service Account allows privilege escalation: a caller
 * without direct access to a Google Drive document can use the MCP tool to read it through the
 * service account's elevated permissions.
 *
 * <p>The service account credentials are provided via the {@code GOOGLE_SERVICE_ACCOUNT_KEY}
 * environment variable (base64-encoded JSON key file content). The Google Drive document ID to read
 * is configured via {@code GOOGLE_DRIVE_DOCUMENT_ID}.
 */
@Slf4j
@RestController
public class Challenge62McpController {

  private static final String JSONRPC_VERSION = "2.0";
  private static final String DEFAULT_KEY_PLACEHOLDER =
      "if_you_see_this_configure_the_google_service_account_properly";
  private static final int MAX_ADDITIONAL_CACHED_DOCUMENTS = 20;
  private static final String DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.readonly";
  private static final String DRIVE_EXPORT_URL =
      "https://www.googleapis.com/drive/v3/files/%s/export?mimeType=text/plain";

  private final String serviceAccountKeyBase64;
  private final String documentId;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  // Cache for fetched Google Drive documents. The configured default document is pinned.
  private final Map<String, String> documentCache;
  // Access-order tracker for non-default document ids to support bounded eviction.
  private final LinkedHashMap<String, Boolean> additionalDocumentCacheOrder;

  @Autowired
  public Challenge62McpController(
      @Value(
              "${GOOGLE_SERVICE_ACCOUNT_KEY:if_you_see_this_configure_the_google_service_account_properly}")
          String serviceAccountKeyBase64,
      @Value("${GOOGLE_DRIVE_DOCUMENT_ID:1PlZkwEd7GouyY4cdOxBuczm6XumQeuZN31LR2BXRgPs}")
          String documentId) {
    this(serviceAccountKeyBase64, documentId, createDefaultRestTemplate(), new ObjectMapper());
  }

  Challenge62McpController(
      String serviceAccountKeyBase64,
      String documentId,
      RestTemplate restTemplate,
      ObjectMapper objectMapper) {
    this.serviceAccountKeyBase64 = serviceAccountKeyBase64;
    this.documentId = documentId;
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.documentCache = new ConcurrentHashMap<>();
    this.additionalDocumentCacheOrder = new LinkedHashMap<>(16, 0.75f, true);
  }

  private static RestTemplate createDefaultRestTemplate() {
    var factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofSeconds(10));
    factory.setReadTimeout(Duration.ofSeconds(10));
    return new RestTemplate(factory);
  }

  @PostMapping(
      value = "/mcp62",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> handleMcpRequest(@RequestBody Map<String, Object> request) {
    String method = (String) request.get("method");
    Object id = request.get("id");
    log.info("Challenge62 MCP request received for method: {}", sanitizeForLog(method));

    return switch (method) {
      case "initialize" -> buildInitializeResponse(id);
      case "tools/list" -> buildToolsListResponse(id);
      case "tools/call" -> handleToolCall(id, request);
      default -> buildErrorResponse(id, -32601, "Method not found: " + method);
    };
  }

  private Map<String, Object> buildInitializeResponse(Object id) {
    return buildResponse(
        id,
        Map.of(
            "protocolVersion",
            "2024-11-05",
            "serverInfo",
            Map.of("name", "wrongsecrets-googledrive-mcp-server", "version", "1.0.0"),
            "capabilities",
            Map.of("tools", Map.of()),
            "instructions",
            "This MCP server provides access to Google Drive documents using a configured service"
                + " account. Use the read_google_drive_document tool to fetch document contents."));
  }

  private Map<String, Object> buildToolsListResponse(Object id) {
    return buildResponse(
        id,
        Map.of(
            "tools",
            List.of(
                Map.of(
                    "name",
                    "read_google_drive_document",
                    "description",
                    "Read the contents of a Google Drive document using the configured service"
                        + " account credentials. The service account has read access to documents"
                        + " that may not be accessible to the caller directly.",
                    "inputSchema",
                    Map.of(
                        "type",
                        "object",
                        "properties",
                        Map.of(
                            "document_id",
                            Map.of(
                                "type",
                                "string",
                                "description",
                                "The Google Drive document ID to read (optional, uses configured"
                                    + " default if not provided)")),
                        "required",
                        List.of())))));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> handleToolCall(Object id, Map<String, Object> request) {
    Map<String, Object> params = (Map<String, Object>) request.get("params");
    if (params == null) {
      return buildErrorResponse(id, -32602, "Missing params");
    }
    String toolName = (String) params.get("name");
    Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
    return switch (toolName) {
      case "read_google_drive_document" -> handleReadGoogleDriveDocument(id, arguments);
      default -> buildErrorResponse(id, -32602, "Unknown tool: " + toolName);
    };
  }

  private Map<String, Object> handleReadGoogleDriveDocument(
      Object id, Map<String, Object> arguments) {
    String docId =
        (arguments != null && arguments.get("document_id") != null)
            ? (String) arguments.get("document_id")
            : documentId;

    if (!isValidGoogleDriveDocumentId(docId)) {
      log.warn("Challenge62: Invalid document ID format: {}", sanitizeForLog(docId));
      return buildErrorResponse(id, -32602, "Invalid document_id format");
    }

    log.info(
        "Challenge62 MCP read_google_drive_document called for document: {}",
        sanitizeForLog(docId));

    if (DEFAULT_KEY_PLACEHOLDER.equals(serviceAccountKeyBase64)) {
      log.warn(
          "Challenge62: GOOGLE_SERVICE_ACCOUNT_KEY is not configured. "
              + "Set this environment variable to a base64-encoded service account JSON key.");
      return buildResponse(
          id,
          Map.of(
              "content",
              List.of(
                  Map.of(
                      "type",
                      "text",
                      "text",
                      "Google Service Account is not configured. "
                          + "Set the GOOGLE_SERVICE_ACCOUNT_KEY environment variable to a "
                          + "base64-encoded service account JSON key file."))));
    }

    try {
      String documentContent = readGoogleDriveDocument(docId);
      return buildResponse(
          id, Map.of("content", List.of(Map.of("type", "text", "text", documentContent))));
    } catch (Exception e) {
      log.error("Challenge62: Failed to read Google Drive document: {}", e.getMessage());
      return buildErrorResponse(id, -32603, "Failed to read document: " + e.getMessage());
    }
  }

  /**
   * Reads a Google Drive document using the configured service account credentials.
   *
   * <p>Caching behavior:
   *
   * <ul>
   *   <li>The configured default document ({@code GOOGLE_DRIVE_DOCUMENT_ID}) is always cached and
   *       never evicted.
   *   <li>At most 20 additional document ids are cached using access-order eviction.
   *   <li>When reading any non-default document, the configured default document is ensured to be
   *       cached alongside it.
   * </ul>
   *
   * @param docId the Google Drive document ID
   * @return the plain text content of the document
   * @throws Exception if the document cannot be read
   */
  String readGoogleDriveDocument(String docId) throws Exception {
    ensureConfiguredDocumentCached(docId);

    String cachedDocument = documentCache.get(docId);
    if (cachedDocument != null) {
      recordDocumentAccess(docId);
      return cachedDocument;
    }

    String documentContent = fetchGoogleDriveDocument(docId);
    cacheDocument(docId, documentContent);
    return documentContent;
  }

  private void ensureConfiguredDocumentCached(String requestedDocId) throws Exception {
    if (documentId.equals(requestedDocId) || documentCache.containsKey(documentId)) {
      return;
    }

    String configuredDocumentContent = fetchGoogleDriveDocument(documentId);
    cacheDocument(documentId, configuredDocumentContent);
  }

  String fetchGoogleDriveDocument(String docId) throws Exception {
    log.info("fetchGoogleDriveDocument called for docId: {}", sanitizeForLog(docId));
    String accessToken = getServiceAccountAccessToken();
    String exportUrl = String.format(DRIVE_EXPORT_URL, docId);

    HttpHeaders headers = new HttpHeaders();
    /**
     * Ensures the configured default document is cached when non-default documents are requested.
     */
    headers.setBearerAuth(accessToken);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
      ResponseEntity<String> response =
          restTemplate.exchange(exportUrl, HttpMethod.GET, entity, String.class);
      String body = response.getBody();
      return body != null ? body.trim() : "";
    } catch (RestClientException e) {
      log.error(
          "Challenge62: Failed to export Google Drive document {}: {}", docId, e.getMessage());
      throw new Exception("Unable to read document from Google Drive: " + e.getMessage(), e);
    }
  }

  /** Adds a document to the bounded cache and evicts only from the non-default document set. */
  private void cacheDocument(String docId, String documentContent) {
    documentCache.put(docId, documentContent);
    if (documentId.equals(docId)) {
      return;
    }

    synchronized (additionalDocumentCacheOrder) {
      additionalDocumentCacheOrder.put(docId, Boolean.TRUE);
      if (additionalDocumentCacheOrder.size() > MAX_ADDITIONAL_CACHED_DOCUMENTS) {
        String evictedDocId = additionalDocumentCacheOrder.keySet().iterator().next();
        additionalDocumentCacheOrder.remove(evictedDocId);
        documentCache.remove(evictedDocId);
      }
    }
  }

  /** Updates access order for non-default cached documents. */
  private void recordDocumentAccess(String docId) {
    if (documentId.equals(docId)) {
      return;
    }

    synchronized (additionalDocumentCacheOrder) {
      if (additionalDocumentCacheOrder.containsKey(docId)) {
        additionalDocumentCacheOrder.get(docId);
      }
    }
  }

  /**
   * Obtains an OAuth2 access token for the configured Google Service Account.
   *
   * @return the OAuth2 access token string
   * @throws Exception if the token cannot be obtained
   */
  private String getServiceAccountAccessToken() throws Exception {
    try {
      byte[] keyBytes = Base64.getDecoder().decode(serviceAccountKeyBase64);
      validateServiceAccountJson(keyBytes);

      ServiceAccountCredentials credentials =
          ServiceAccountCredentials.fromStream(new ByteArrayInputStream(keyBytes));
      ServiceAccountCredentials scopedCredentials =
          (ServiceAccountCredentials)
              credentials.createScoped(Collections.singletonList(DRIVE_SCOPE));
      scopedCredentials.refreshIfExpired();
      return scopedCredentials.getAccessToken().getTokenValue();
    } catch (IllegalArgumentException e) {
      throw new Exception("Invalid base64 encoding for GOOGLE_SERVICE_ACCOUNT_KEY", e);
    }
  }

  /**
   * Validates that the decoded bytes represent a valid JSON object with the expected service
   * account fields. This guards against injection of arbitrary content.
   *
   * @param keyBytes the decoded service account JSON bytes
   * @throws Exception if the JSON is invalid or missing required fields
   */
  private void validateServiceAccountJson(byte[] keyBytes) throws Exception {
    try {
      JsonNode root = objectMapper.readTree(new String(keyBytes, StandardCharsets.UTF_8));
      if (!root.isObject()) {
        throw new Exception("Service account key must be a JSON object");
      }
      if (!root.has("type") || !"service_account".equals(root.get("type").asText())) {
        throw new Exception("Invalid service account key: missing or incorrect 'type' field");
      }
      if (!root.has("client_email") || !root.has("private_key")) {
        throw new Exception(
            "Invalid service account key: missing required fields (client_email, private_key)");
      }
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new Exception("Service account key is not valid JSON", e);
    }
  }

  private String sanitizeForLog(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("[\r\n\u0085\u2028\u2029]", "_");
  }

  /**
   * Validates that a Google Drive document ID only contains characters that are valid in a Google
   * Drive document ID. Document IDs consist of alphanumeric characters, hyphens and underscores.
   * This prevents SSRF by ensuring the ID cannot be used to escape the expected URL path.
   *
   * @param docId the document ID to validate
   * @return true if the document ID is valid
   */
  private boolean isValidGoogleDriveDocumentId(String docId) {
    return docId != null && !docId.isEmpty() && docId.matches("[a-zA-Z0-9_\\-]+");
  }

  private Map<String, Object> buildResponse(Object id, Object result) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("jsonrpc", JSONRPC_VERSION);
    response.put("id", id);
    response.put("result", result);
    return response;
  }

  private Map<String, Object> buildErrorResponse(Object id, int code, String message) {
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("jsonrpc", JSONRPC_VERSION);
    response.put("id", id);
    response.put("error", Map.of("code", code, "message", message));
    return response;
  }
}
