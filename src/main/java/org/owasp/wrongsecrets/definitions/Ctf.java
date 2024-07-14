package org.owasp.wrongsecrets.definitions;

/**
 * Defines if a challenge is a CTF challenge or not. Later on we might add more types of challenges
 * which needs to have more configuration, that's why we have this as a record.
 *
 * @param enabled true if the challenge is a CTF challenge
 */
public record Ctf(boolean enabled) {}
