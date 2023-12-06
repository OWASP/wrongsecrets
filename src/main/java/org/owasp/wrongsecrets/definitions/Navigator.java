package org.owasp.wrongsecrets.definitions;

import java.util.List;
import java.util.Optional;

/** Encapsulate the navigation, we can optimise this later. */
public record Navigator(List<ChallengeDefinition> challenges, ChallengeDefinition current) {

  public Optional<ChallengeDefinition> next() {
    return navigate(1);
  }

  public Optional<ChallengeDefinition> previous() {
    return navigate(-1);
  }

  private Optional<ChallengeDefinition> navigate(int direction) {
    int index = challenges.indexOf(current);
    int navigationIndex = index + direction;

    if (navigationIndex < 0 || navigationIndex >= challenges.size()) {
      return Optional.empty();
    }
    return Optional.of(challenges.get(navigationIndex));
  }
}
