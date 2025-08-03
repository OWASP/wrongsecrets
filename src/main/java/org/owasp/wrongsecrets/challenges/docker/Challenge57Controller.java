package org.owasp.wrongsecrets.challenges.docker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for Challenge 57 to serve LLM JavaScript with exposed API key. */
@Slf4j
@RestController
@RequiredArgsConstructor
public class Challenge57Controller {

  private final Challenge57 challenge;

  /**
   * Endpoint to serve JavaScript code that contains an exposed LLM API key. This simulates how
   * developers accidentally expose API keys in client-side code.
   */
  @GetMapping(value = "/llm-chat.js", produces = MediaType.APPLICATION_JAVASCRIPT_VALUE)
  public String getLLMJavaScript() {
    log.info("Serving LLM JavaScript for Challenge 57...");
    return challenge.getLLMJavaScriptCode();
  }

  /** Endpoint to serve a simple HTML page that loads the vulnerable JavaScript. */
  @GetMapping(value = "/llm-demo", produces = MediaType.TEXT_HTML_VALUE)
  public String getLLMDemoPage() {
    return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LLM Chat Demo - Challenge 57</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; }
        #chat-output { border: 1px solid #ddd; height: 300px; overflow-y: auto; padding: 10px; margin-bottom: 10px; }
        #message-input { width: 70%; padding: 5px; }
        #send-button { width: 25%; padding: 5px; }
        .llm-response { background: #f0f0f0; margin: 5px 0; padding: 5px; border-radius: 3px; }
        .error-message { background: #ffe6e6; color: red; margin: 5px 0; padding: 5px; border-radius: 3px; }
        .warning { background: #fff3cd; color: #856404; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
    </style>
</head>
<body>
    <h1>🤖 LLM Chat Demo</h1>
    <div class="warning">
        <strong>⚠️ Security Notice:</strong> This demo application contains a common security vulnerability.
        Can you find the exposed API key?
    </div>

    <div id="chat-output"></div>
    <div>
        <input type="text" id="message-input" placeholder="Type your message here..." />
        <button id="send-button">Send</button>
    </div>

    <div style="margin-top: 20px; font-size: 0.9em; color: #666;">
        <p><strong>Hint:</strong> Check the browser's developer tools (F12) and look at the Network tab or Console.</p>
        <p>You can also view the source code of the JavaScript file directly.</p>
    </div>

    <script src="/llm-chat.js"></script>
</body>
</html>
""";
  }
}
