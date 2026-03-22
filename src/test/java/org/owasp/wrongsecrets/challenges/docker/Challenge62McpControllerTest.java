package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class Challenge62McpControllerTest {

  private static final String DEFAULT_KEY =
      "if_you_see_this_configure_the_google_service_account_properly";
  private static final String DEFAULT_DOC_ID = "1PlZkwEd7GouyY4cdOxBuczm6XumQeuZN31LR2BXRgPs";

  @Test
  void initializeShouldReturnServerInfo() {
    var controller =
        new Challenge62McpController(DEFAULT_KEY, DEFAULT_DOC_ID, mock(RestTemplate.class), new ObjectMapper());
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 1, "method", "initialize");

    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("result");
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    assertThat(result).containsKey("serverInfo");
    @SuppressWarnings("unchecked")
    Map<String, Object> serverInfo = (Map<String, Object>) result.get("serverInfo");
    assertThat(serverInfo.get("name")).isEqualTo("wrongsecrets-googledrive-mcp-server");
  }

  @Test
  void toolsListShouldExposeReadGoogleDriveDocumentTool() {
    var controller =
        new Challenge62McpController(DEFAULT_KEY, DEFAULT_DOC_ID, mock(RestTemplate.class), new ObjectMapper());
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 1, "method", "tools/list");

    Map<String, Object> response = controller.handleMcpRequest(request);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> tools = (List<Map<String, Object>>) result.get("tools");
    assertThat(tools).hasSize(1);
    assertThat(tools.get(0).get("name")).isEqualTo("read_google_drive_document");
  }

  @Test
  void readDocumentShouldReturnNotConfiguredMessageWhenKeyIsDefault() {
    var controller =
        new Challenge62McpController(DEFAULT_KEY, DEFAULT_DOC_ID, mock(RestTemplate.class), new ObjectMapper());
    Map<String, Object> request =
        Map.of(
            "jsonrpc",
            "2.0",
            "id",
            2,
            "method",
            "tools/call",
            "params",
            Map.of("name", "read_google_drive_document", "arguments", Map.of()));

    Map<String, Object> response = controller.handleMcpRequest(request);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> content = (List<Map<String, Object>>) result.get("content");
    assertThat(content.get(0).get("text").toString())
        .contains("Google Service Account is not configured");
  }

  @Test
  void unknownMethodShouldReturnError() {
    var controller =
        new Challenge62McpController(DEFAULT_KEY, DEFAULT_DOC_ID, mock(RestTemplate.class), new ObjectMapper());
    Map<String, Object> request =
        Map.of("jsonrpc", "2.0", "id", 1, "method", "unknown/method");

    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("error");
    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) response.get("error");
    assertThat(error.get("code")).isEqualTo(-32601);
  }

  @Test
  void unknownToolShouldReturnError() {
    var controller =
        new Challenge62McpController(DEFAULT_KEY, DEFAULT_DOC_ID, mock(RestTemplate.class), new ObjectMapper());
    Map<String, Object> request =
        Map.of(
            "jsonrpc",
            "2.0",
            "id",
            2,
            "method",
            "tools/call",
            "params",
            Map.of("name", "nonexistent_tool", "arguments", Map.of()));

    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("error");
    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) response.get("error");
    assertThat(error.get("code")).isEqualTo(-32602);
  }

  @Test
  void readDocumentShouldCallGoogleDriveApiWhenKeyIsConfigured() throws Exception {
    var restTemplate = mock(RestTemplate.class);
    // Provide a mock service account key (valid base64-encoded JSON structure)
    // We spy on readGoogleDriveDocument to avoid actual credential resolution
    var controller =
        new Challenge62McpController(DEFAULT_KEY, DEFAULT_DOC_ID, restTemplate, new ObjectMapper()) {
          @Override
          String readGoogleDriveDocument(String docId) {
            return "secret_from_google_drive_document";
          }
        };

    Map<String, Object> request =
        Map.of(
            "jsonrpc",
            "2.0",
            "id",
            2,
            "method",
            "tools/call",
            "params",
            Map.of(
                "name",
                "read_google_drive_document",
                "arguments",
                Map.of("document_id", "testDocId")));

    // Override the key check by using a non-default key
    var controllerWithKey =
        new Challenge62McpController("dGVzdA==", DEFAULT_DOC_ID, restTemplate, new ObjectMapper()) {
          @Override
          String readGoogleDriveDocument(String docId) {
            return "secret_from_google_drive_document";
          }
        };

    Map<String, Object> response = controllerWithKey.handleMcpRequest(request);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> content = (List<Map<String, Object>>) result.get("content");
    assertThat(content.get(0).get("text")).isEqualTo("secret_from_google_drive_document");
  }
}
