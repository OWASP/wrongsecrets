package org.owasp.wrongsecrets.challenges.docker;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * MCP (Model Context Protocol) server endpoint for Challenge 60. Demonstrates how an insecure MCP
 * server with a shell execution tool can expose environment variables including secrets.
 */
@Slf4j
@RestController
public class Challenge60McpController {

  private static final String JSONRPC_VERSION = "2.0";

  /** Handles incoming MCP JSON-RPC 2.0 requests. */
  @PostMapping(
      value = "/mcp",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> handleMcpRequest(@RequestBody Map<String, Object> request) {
    String method = (String) request.get("method");
    Object id = request.get("id");
    log.warn("MCP request received for method: {}", sanitizeForLog(method));

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
            Map.of("name", "wrongsecrets-mcp-server", "version", "1.0.0"),
            "capabilities",
            Map.of("tools", Map.of())));
  }

  private Map<String, Object> buildToolsListResponse(Object id) {
    return buildResponse(
        id,
        Map.of(
            "tools",
            List.of(
                Map.of(
                    "name",
                    "execute_command",
                    "description",
                    "Execute a shell command on the server",
                    "inputSchema",
                    Map.of(
                        "type",
                        "object",
                        "properties",
                        Map.of(
                            "command",
                            Map.of(
                                "type", "string", "description", "The shell command to execute")),
                        "required",
                        List.of("command"))))));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> handleToolCall(Object id, Map<String, Object> request) {
    Map<String, Object> params = (Map<String, Object>) request.get("params");
    if (params == null) {
      return buildErrorResponse(id, -32602, "Missing params");
    }
    String toolName = (String) params.get("name");
    if (!"execute_command".equals(toolName)) {
      return buildErrorResponse(id, -32602, "Unknown tool: " + toolName);
    }

    Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
    String command = arguments != null ? (String) arguments.get("command") : "";
    log.warn("MCP execute_command tool called with command: {}", sanitizeForLog(command));

    // Return the process environment variables to simulate what an insecure MCP server would
    // expose when an attacker runs a command like "env" or "printenv"
    String envOutput =
        System.getenv().entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("\n"));

    return buildResponse(id, Map.of("content", List.of(Map.of("type", "text", "text", envOutput))));
  }

  private String sanitizeForLog(String input) {
    if (input == null) {
      return null;
    }
    return input.replaceAll("[\r\n\u0085\u2028\u2029]", "_");
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
