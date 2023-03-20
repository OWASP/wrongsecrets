package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Challenge29Test {

    @Mock
    private ScoreCard scoreCard;
    @Test
    public void testGetMyString() {
        Challenge29 controller = new Challenge29(scoreCard);
        String expected = "ThisIsYourPasswordOfChallenge30";
        String result = controller.getMyString();
        assertEquals(expected, result);
    }
}
