package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
public class Challenge8Test {

    @Mock
    private ScoreCard scoreCard;

    @Test
    void spoilerShouldRevealAnswerAndSolveAnswer() {
        var challenge = new Challenge8(scoreCard);

        Assertions.assertThat(challenge.spoiler().solution().length()).isEqualTo(10);
        Assertions.assertThat(challenge.solved((challenge.spoiler().solution()))).isTrue();
        Mockito.verify(scoreCard).completeChallenge(challenge);
    }


}
