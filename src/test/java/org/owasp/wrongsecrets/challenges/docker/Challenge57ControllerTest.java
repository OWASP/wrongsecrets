package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Challenge57ControllerTest {

  @Mock private Challenge57 challenge;

  @InjectMocks private Challenge57Controller controller;

  @Test
  void getLLMJavaScriptShouldReturnJavaScriptCode() {
    // Given
    String expectedJs = "// AI Chat Application - Client-side JavaScript\nclass LLMChatApp {";
    org.mockito.Mockito.when(challenge.getLLMJavaScriptCode()).thenReturn(expectedJs);

    // When
    String result = controller.getLLMJavaScript();

    // Then
    assertThat(result).isEqualTo(expectedJs);
    org.mockito.Mockito.verify(challenge).getLLMJavaScriptCode();
  }

  @Test
  void getLLMDemoPageShouldReturnHTMLPage() {
    // When
    String result = controller.getLLMDemoPage();

    // Then
    assertThat(result).contains("<!DOCTYPE html>");
    assertThat(result).contains("LLM Chat Demo");
    assertThat(result).contains("Security Notice");
    assertThat(result).contains("/llm-chat.js");
  }
}
