package org.owasp.wrongsecrets.definitions;

import java.util.regex.Pattern;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * Name of a challenge, @see {@link ChallengeConfig#nameConverter()} for more information.
 *
 * @param name the name of the challenge from the configuration, the full class name
 * @param url the url under which the challenge is available.
 */
public record ChallengeName(String name, String url) {

  public boolean partialMatches(String nameToMatch) {
    return LevenshteinDistance.getDefaultInstance().apply(url, nameToMatch) <= 1;
  }
}
