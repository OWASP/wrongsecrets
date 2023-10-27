package org.owasp.wrongsecrets.definitions;

import java.util.regex.Pattern;

/**
 * Name of a challenge, @see {@link ChallengeConfig#nameConverter()} for more information.
 *
 * @param name the name of the challenge from the configuration, the full class name
 * @param url the url under which the challenge is available.
 */
public record ChallengeName(String name, String url) {
  private static final Pattern digit = Pattern.compile(".*\\d.*");

  public boolean partialMatches(String nameToMatch) {
    var digitMatcher = digit.matcher(nameToMatch);
    if (digitMatcher.matches()) {
      return name.contains(digitMatcher.group()) || url.contains(digitMatcher.group());
    }
    return false;
  }
}
