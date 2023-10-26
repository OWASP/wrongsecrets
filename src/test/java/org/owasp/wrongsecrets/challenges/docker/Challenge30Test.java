package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge30Test {

  @Test
  void testSpoiler() {
    ScoreCard scoreCard = Mockito.mock(ScoreCard.class);
    Challenge30 challenge = new Challenge30(scoreCard);

    Spoiler spoiler = challenge.spoiler();

    Assertions.assertNotNull(spoiler);
    Assertions.assertNotNull(spoiler.solution());
    Assertions.assertEquals(12, spoiler.solution().length());
  }
}
