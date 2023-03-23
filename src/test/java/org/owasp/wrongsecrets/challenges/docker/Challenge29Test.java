package org.owasp.wrongsecrets.challenges.docker;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

import static org.junit.jupiter.api.Assertions.*;
public class Challenge29Test {

    @Mock
    private ScoreCard scoreCard;
    @Test
    public void testGetMyString() {
        Challenge29 challenge = new Challenge29(scoreCard);
        String result = challenge.getMyString();
        assertEquals(10, result.length(), "Length of string should be 10.");
        assertTrue(result.matches("[0-9A-Za-z]+"), "String should only have alphanumeric characters");
    }

    @Test
    public void testGenerateRandomString() {
        Challenge29 challenge = new Challenge29(scoreCard);
        String result = challenge.generateRandomString(5);
        assertEquals(5, result.length(), "Length of string should be 5.");
        assertTrue(result.matches("[0-9A-Za-z]+"), "String should only have alphanumeric characters.");
    }

}
