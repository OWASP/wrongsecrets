package org.owasp.wrongsecrets.challenges.kubernetes.llama;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class LlamaService {

  private final RestClient restClient;

  public LlamaService(@Value("${llama.url:http://localhost:1234}") String llamaUrl) {

    this.restClient = RestClient.builder().baseUrl(llamaUrl).build();
  }

  public String chat(String systemPrompt, String userMessage) {
    ChatRequest request =
        new ChatRequest(
            "local-model",
            List.of(new Message("system", systemPrompt), new Message("user", userMessage)),
            0.1);
    log.info("Request messages: {}", request.messages());

    ChatResponse response =
        restClient
            .post()
            .uri("/v1/chat/completions")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(ChatResponse.class);

    if (response == null || response.choices() == null || response.choices().isEmpty()) {
      throw new IllegalStateException("No response received from llama-server");
    }

    Message responseMessage = response.choices().getFirst().message();
    log.info("Response message: {}", responseMessage);

    return responseMessage.content();
  }

  public record ChatRequest(String model, List<Message> messages, double temperature) {}

  public record Message(String role, String content) {}

  public record ChatResponse(List<Choice> choices) {}

  public record Choice(Message message) {}
}
