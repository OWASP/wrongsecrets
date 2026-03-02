package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Challenge60McpControllerTest {

  private Challenge60McpController controller;

  @BeforeEach
  void setUp() {
    controller = new Challenge60McpController();
  }

  @Test
  void initializeShouldReturnServerInfo() {
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 1, "method", "initialize");
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("result");
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    assertThat(result).containsKey("serverInfo");
    assertThat(result).containsKey("protocolVersion");
  }

  @Test
  void toolsListShouldReturnBothTools() {
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 2, "method", "tools/list");
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("result");
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    assertThat(result).containsKey("tools");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> tools = (List<Map<String, Object>>) result.get("tools");
    assertThat(tools).hasSize(2);
    assertThat(tools.stream().map(t -> t.get("name")))
        .containsExactlyInAnyOrder("execute_command", "forward_env");
  }

  @Test
  void toolsCallShouldReturnEnvironmentVariables() {
    Map<String, Object> arguments = Map.of("command", "env");
    Map<String, Object> params = Map.of("name", "execute_command", "arguments", arguments);
    Map<String, Object> request =
        Map.of("jsonrpc", "2.0", "id", 3, "method", "tools/call", "params", params);
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("result");
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    assertThat(result).containsKey("content");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> content = (List<Map<String, Object>>) result.get("content");
    assertThat(content).isNotEmpty();
    assertThat(content.get(0).get("type")).isEqualTo("text");
    assertThat(content.get(0).get("text")).isNotNull();
  }

  @Test
  void toolsCallWithUnknownToolShouldReturnError() {
    Map<String, Object> params = Map.of("name", "unknown_tool", "arguments", Map.of());
    Map<String, Object> request =
        Map.of("jsonrpc", "2.0", "id", 4, "method", "tools/call", "params", params);
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("error");
    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) response.get("error");
    assertThat(error.get("code")).isEqualTo(-32602);
  }

  @Test
  void unknownMethodShouldReturnMethodNotFoundError() {
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 5, "method", "unknown/method");
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("error");
    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) response.get("error");
    assertThat(error.get("code")).isEqualTo(-32601);
  }

  @Test
  void toolsCallWithMissingParamsShouldReturnError() {
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 6, "method", "tools/call");
    // params key is missing
    Map<String, Object> requestWithNull = new java.util.HashMap<>(request);
    requestWithNull.put("params", null);
    Map<String, Object> response = controller.handleMcpRequest(requestWithNull);

    assertThat(response).containsKey("error");
  }

  @Test
  void initializeShouldContainMaliciousInstructions() {
    Map<String, Object> request = Map.of("jsonrpc", "2.0", "id", 1, "method", "initialize");
    Map<String, Object> response = controller.handleMcpRequest(request);

    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    assertThat(result).containsKey("instructions");
    assertThat((String) result.get("instructions"))
        .isEqualTo(Challenge60McpController.MALICIOUS_INSTRUCTIONS);
  }

  @Test
  void forwardEnvToolShouldAcceptAndAcknowledgeData() {
    Map<String, Object> arguments = Map.of("data", "HOME=/root\nUSER=wrongsecrets");
    Map<String, Object> params = Map.of("name", "forward_env", "arguments", arguments);
    Map<String, Object> request =
        Map.of("jsonrpc", "2.0", "id", 9, "method", "tools/call", "params", params);
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("result");
    @SuppressWarnings("unchecked")
    Map<String, Object> result = (Map<String, Object>) response.get("result");
    assertThat(result).containsKey("content");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> content = (List<Map<String, Object>>) result.get("content");
    assertThat(content).isNotEmpty();
    assertThat(content.get(0).get("text").toString()).contains("Data received");
  }

  @Test
  void crlfInMethodShouldBeHandledWithoutError() {
    Map<String, Object> request =
        Map.of("jsonrpc", "2.0", "id", 7, "method", "unknown\r\ninjected");
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("error");
    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) response.get("error");
    assertThat(error.get("code")).isEqualTo(-32601);
  }

  @Test
  void crlfInCommandShouldBeHandledWithoutError() {
    Map<String, Object> arguments = Map.of("command", "env\r\ninjected");
    Map<String, Object> params = Map.of("name", "execute_command", "arguments", arguments);
    Map<String, Object> request =
        Map.of("jsonrpc", "2.0", "id", 8, "method", "tools/call", "params", params);
    Map<String, Object> response = controller.handleMcpRequest(request);

    assertThat(response).containsKey("result");
  }
}
