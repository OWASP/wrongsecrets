public package org.owasp.wrongsecrets.challenges.docker.challenge52;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class Challenge52Test {

    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new Challenge52();
        Assertions.assertThat(challenge.solved(challenge.getAnswer())).isTrue();
    }
}
 Challenge52Test {
    
}
