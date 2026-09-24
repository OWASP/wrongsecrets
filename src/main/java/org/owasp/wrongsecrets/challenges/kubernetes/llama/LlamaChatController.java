package org.owasp.wrongsecrets.challenges.kubernetes.llama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/challenges/llama")
public class LlamaChatController {

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final ObjectMapper objectMapper;
  private final String llamaUrl;

  public LlamaChatController(ObjectMapper objectMapper, @Value("${LLAMAURL}") String llamaUrl) {
    this.objectMapper = objectMapper;
    this.llamaUrl = llamaUrl;
  }

  @PostMapping(
      value = "/chat",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ChatResponse chat(@RequestBody ChatRequest request) throws Exception {

    ObjectNode llamaRequest = objectMapper.createObjectNode();
    llamaRequest.put("temperature", 0.1);
    llamaRequest.put("max_tokens", 128);
    var userMessage = llamaRequest.putArray("messages").addObject();
    userMessage.put("role", "user");
    userMessage.put("content", request.message());

    var httpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(llamaUrl + "/v1/chat/completions"))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(llamaRequest)))
            .build();

    var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new IllegalStateException("Llama server returned HTTP " + response.statusCode());
    }

    JsonNode json = objectMapper.readTree(response.body());

    String answer = json.path("choices").path(0).path("message").path("content").asText();

    return new ChatResponse(answer);
  }

  public record ChatRequest(String message) {}

  public record ChatResponse(String response) {}
}
