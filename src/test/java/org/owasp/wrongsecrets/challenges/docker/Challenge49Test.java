package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

import static org.assertj.core.api.Assertions.assertThat;

class Challenge49Test {

    @Test
    void spoilerShouldGiveAnswer() {
        var challenge = new Challenge49("test");

        assertThat(challenge.spoiler()).isEqualTo(new Spoiler("test"));
    }

    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new Challenge49("test");

        assertThat(challenge.answerCorrect("test")).isTrue();
    }

    @Test
    void incorrectAnswerShouldNotSolveChallenge() {
        var challenge = new Challenge49("test");

        assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    }


}
