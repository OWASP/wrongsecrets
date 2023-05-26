package org.owasp.wrongsecrets.challenges;

/**
 * Used to communicate with the front-end.
 *
 * @param solution as provided throught the form.
 */
public record ChallengeForm(String solution) {}
