package org.owasp.wrongsecrets.canaries;

/**
 * Used for counting the number of canary-token based callbacks.
 *
 * @see org.owasp.wrongsecrets.StatsController for the tracking controller
 * @see org.owasp.wrongsecrets.canaries.CanariesController for the calling controler
 */
public interface CanaryCounter {

  /** incement the counter of callbacks with 1. */
  void upCallBackCounter();

  /**
   * Gets the total number of canary token callback calls.
   *
   * @return int ≥ 0
   */
  int getTotalCount();

  /**
   * Sets the content of the last token its contents as a Strings.
   *
   * @param tokenContent unprocessed contents of the callback (E.g. full data of the canarytoken)
   */
  void setLastCanaryToken(String tokenContent);

  /**
   * Returns the last token given during the callback invocation.
   *
   * @return unprocessed callback token contents
   */
  String getLastToken();
}
