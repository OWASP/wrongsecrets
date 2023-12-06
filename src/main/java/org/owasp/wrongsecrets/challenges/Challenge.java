package org.owasp.wrongsecrets.challenges;

/** General Abstract Challenge class containing all the necessary members for a challenge. */
public interface Challenge {

  /**
   * Returns a Spoiler object containing the secret for the challenge.
   *
   * @return Spoiler with anser
   */
  Spoiler spoiler();

  /**
   * method that needs to be overwritten by the Challenge implementation class to do the actual
   * evaluation of the answer.
   *
   * @param answer String provided by the user
   * @return true if answer is Correct
   */
  boolean answerCorrect(String answer);
}
