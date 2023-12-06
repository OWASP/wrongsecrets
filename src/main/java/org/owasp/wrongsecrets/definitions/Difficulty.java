package org.owasp.wrongsecrets.definitions;

/**
 * Defines the difficulty of a challenge, as defined in the yaml configuration.
 *
 * @param difficulty description of the difficulty level, see
 *     `src/main/resources/wrong-secrets-configuration.yml`
 */
public record Difficulty(String difficulty) {}
