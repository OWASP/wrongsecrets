package org.owasp.wrongsecrets.challenges;

/**
 * Class used to return spoilers with the actual answer. Spoilers are used for quick testing and for
 * finding the answer if a tutorial fails. Please note that spoiling is disabled in CTF mode, and
 * there is the setting
 *
 * @param solution the actual solution
 */
public record Spoiler(String solution) {}
