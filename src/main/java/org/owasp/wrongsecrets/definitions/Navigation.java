package org.owasp.wrongsecrets.definitions;

import java.util.List;
import java.util.Optional;

/**
 * Encapsulate the navigation
 *
 * <p>This makes it possible for having a <code>
 * Map<ChallengeDefinition, Pair<ChallengeDefinition, ChallengeDefinition></code> without changing
 * the calling side
 */
public record Navigation(List<ChallengeDefinition> challenges, ChallengeDefinition current) {

  public Optional<ChallengeDefinition> next() {
    return navigate(1);
  }

  public Optional<ChallengeDefinition> previous() {
    return navigate(-1);
  }

  private Optional<ChallengeDefinition> navigate(int direction) {
    int index = challenges.indexOf(current);
    if (index == -1) {
      return Optional.empty();
    }
    if (index == challenges.size() - 1 || direction == -1
        ? index == 0
        : index == challenges.size() - 1) {
      return Optional.empty();
    }
    return Optional.of(challenges.get((index + direction) % challenges.size()));
  }
}
