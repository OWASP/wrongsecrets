package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.spongycastle.util.encoders.Hex;

@ExtendWith(MockitoExtension.class)
class Challenge31Test {

    private final String secretKey = new String(Hex.decode("534746325a53426849473570593255675a474635"));
    @Mock
    private ScoreCard scoreCard;

    @Test
    void spoilerShouldRevealAnswer() {
        var challenge = new Challenge31(scoreCard);

        Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler(secretKey));
    }

    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new Challenge31(scoreCard);

        Assertions.assertThat(challenge.solved(secretKey)).isTrue();
        Mockito.verify(scoreCard).completeChallenge(challenge);
    }

    @Test
    void incorrectAnswerShouldNotSolveChallenge() {
        var challenge = new Challenge31(scoreCard);

        Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
        Mockito.verifyNoInteractions(scoreCard);
    }
}
