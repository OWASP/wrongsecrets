package org.owasp.wrongsecrets.challenges.docker;

import java.util.List;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This is a challenge based on leaking secrets with the misuse of Git notes */
@Component
@Order(38)
public class Challenge38 extends Challenge {

  public Challenge38(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSolution());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getSolution().equals(answer);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} Git based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.GIT.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
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
