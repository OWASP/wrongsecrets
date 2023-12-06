package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** This is a challenge based on leaking secrets with the misuse of Git notes. */
@Component
public class Challenge38 implements Challenge {

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSolution());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getSolution().equals(answer);
  }

  private String getSolution() {
    return unobfuscate("UOZFGZTLOLLXHTKEGGS");
  }

  private String unobfuscate(String obfuscatedString) {
    final String key = "QWERTYUIOPASDFGHJKLZXCVBNM";
    StringBuilder plainText = new StringBuilder();
    for (char c : obfuscatedString.toCharArray()) {
      if (Character.isLetter(c)) {
        int index = key.indexOf(Character.toUpperCase(c));
        char replacement = (char) ('A' + index);
        plainText.append(replacement);
      } else {
        plainText.append(c);
        System.out.println(plainText);
      }
    }
    return plainText.toString();
  }
}
