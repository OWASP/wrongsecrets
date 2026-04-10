package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.docker.challenge63.Challenge63;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class Challenge63Test {

    @Test
    void testAnswerIsCorrect() {
        Challenge63 challenge = new Challenge63();
        assertTrue(challenge.answerCorrect("wh0sp1ay1ngwrongs3cr3ts"));
    }

    @Test
    void testWrongAnswerIsRejected() {
        Challenge63 challenge = new Challenge63();
        assertFalse(challenge.answerCorrect("wronganswer"));
    }

    @Test
    void testSpoilerRevealsAnswer() {
        Challenge63 challenge = new Challenge63();
        assertEquals("wh0sp1ay1ngwrongs3cr3ts", challenge.spoiler().solution());
    }
}