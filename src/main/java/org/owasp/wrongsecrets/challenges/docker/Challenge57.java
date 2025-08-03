package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** Challenge demonstrating LLM API key exposure in client-side JavaScript. */
@Slf4j
@Component
public class Challenge57 implements Challenge {

  // Simulated LLM API key that would be exposed in client-side JavaScript
  private static final String LLM_API_KEY =
      "sk-llm-api-key-abc123def456ghi789jkl012mno345pqr678stu901vwx234yzA";

  @Override
  public Spoiler spoiler() {
    return new Spoiler(LLM_API_KEY);
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer) && LLM_API_KEY.equals(answer.trim());
  }

  /**
   * This method returns JavaScript code that would typically be served to the browser, containing
   * an exposed LLM API key. This demonstrates how sensitive API keys can be accidentally exposed in
   * client-side code.
   */
  public String getLLMJavaScriptCode() {
    return """
           // AI Chat Application - Client-side JavaScript
           class LLMChatApp {
               constructor() {
                   // WARNING: This is a security anti-pattern!
                   // API keys should NEVER be exposed in client-side code
                   this.apiKey = '%s';
                   this.apiEndpoint = 'https://api.example-llm.com/v1/chat/completions';
                   this.initializeChat();
               }

               async initializeChat() {
                   console.log('Initializing LLM chat with API key:', this.apiKey);
                   this.setupEventListeners();
               }

               setupEventListeners() {
                   document.getElementById('send-button').addEventListener('click', () => {
                       this.sendMessage();
                   });

                   document.getElementById('message-input').addEventListener('keypress', (e) => {
                       if (e.key === 'Enter') {
                           this.sendMessage();
                       }
                   });
               }

               async sendMessage() {
                   const messageInput = document.getElementById('message-input');
                   const message = messageInput.value.trim();

                   if (!message) return;

                   try {
                       const response = await fetch(this.apiEndpoint, {
                           method: 'POST',
                           headers: {
                               'Authorization': `Bearer ${this.apiKey}`,
                               'Content-Type': 'application/json'
                           },
                           body: JSON.stringify({
                               model: 'gpt-3.5-turbo',
                               messages: [
                                   {role: 'user', content: message}
                               ],
                               max_tokens: 150
                           })
                       });

                       const data = await response.json();
                       this.displayResponse(data.choices[0].message.content);

                   } catch (error) {
                       console.error('LLM API Error:', error);
                       console.log('Failed request used API key:', this.apiKey);
                       this.displayError('Failed to get response from LLM service');
                   }

                   messageInput.value = '';
               }

               displayResponse(text) {
                   const chatOutput = document.getElementById('chat-output');
                   const responseDiv = document.createElement('div');
                   responseDiv.className = 'llm-response';
                   responseDiv.textContent = text;
                   chatOutput.appendChild(responseDiv);
                   chatOutput.scrollTop = chatOutput.scrollHeight;
               }

               displayError(error) {
                   const chatOutput = document.getElementById('chat-output');
                   const errorDiv = document.createElement('div');
                   errorDiv.className = 'error-message';
                   errorDiv.textContent = error;
                   chatOutput.appendChild(errorDiv);
               }
           }

           // Initialize the chat app when page loads
           document.addEventListener('DOMContentLoaded', () => {
               // Debug: Log the API key for development (another anti-pattern!)
               console.log('Debug: LLM API Key = %s');
               new LLMChatApp();
           });
           """
        .formatted(LLM_API_KEY, LLM_API_KEY);
  }
}
