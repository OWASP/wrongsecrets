package org.owasp.wrongsecrets.definitions;

import java.util.Objects;

/**
 * Defines an environment for a challenge.
 *
 * @param name the name of the environment used in the code
 * @param ctf the name of the environment when used in the CTF context
 * @param overview the name of the environment when used in the overview
 * @param displayName the name of the environment when used in the UI
 * @param missingEnvironment the location of the missing environment message
 */
public record Environment(
    String name,
    String ctf,
    String overview,
    String displayName,
    Sources.TextWithFileLocation missingEnvironment) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Environment that = (Environment) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
