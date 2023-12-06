package org.owasp.wrongsecrets.definitions;

/**
 * Name of a challenge, @see {@link ChallengeConfig#nameConverter()} for more information.
 *
 * @param name the name of the challenge from the configuration, the full class name
 * @param shortName the short name under which the challenge is available. Used in the URL for
 *     example.
 */
public record ChallengeName(String name, String shortName) {}
