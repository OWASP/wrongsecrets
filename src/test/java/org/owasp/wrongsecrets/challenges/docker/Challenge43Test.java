package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge43Test {

    private final String secretKey =
        new String(Hex.decode("4348524953544D4153324B3233"));

    @Test
    void spoilerShouldRevealAnswer() {
        var challenge = new Challenge43();

        assertThat(challenge.spoiler()).isEqualTo(new Spoiler(secretKey));
    }

    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new Challenge43();

        assertThat(challenge.answerCorrect(secretKey)).isTrue();
    }

    @Test
    void incorrectAnswerShouldNotSolveChallenge() {
        var challenge = new Challenge43();

        assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    }
}
