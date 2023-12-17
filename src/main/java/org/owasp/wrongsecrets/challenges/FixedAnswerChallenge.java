package org.owasp.wrongsecrets.challenges;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Objects;

/**
 * Use this class when a challenge where the answer is fixed, meaning it will not change and does
 * not depend on the given answer. For example: a hardcoded key or Spring environment variable.
 *
 * <p>Why do we make this distinction? Because in the case of the fixed answer we can cache the
 * value. It is important to <b>NOT</b> do any reading / calculation in the constructor when using
 * this interface.
 *
 * <p>NOTE: If the challenge depends on a calculation you can implement {@link Challenge}
 */
public abstract class FixedAnswerChallenge implements Challenge {

  private Supplier<String> cachedAnswer = Suppliers.memoize(() -> getAnswer());

  @Override
  public final Spoiler spoiler() {
    return new Spoiler(cachedAnswer.get());
  }

  @Override
  public final boolean answerCorrect(String answer) {
    return Objects.equals(cachedAnswer.get(), answer);
  }

  public abstract String getAnswer();
}
