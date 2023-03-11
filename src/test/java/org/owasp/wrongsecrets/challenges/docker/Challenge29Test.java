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
class Challenge28Test {

    @Mock
    private ScoreCard scoreCard;
    private final String passcode =new String(Hex.decode("6d6f697364666e6f7765793233346669333269636f38617177343132"));
    @Test
    void spoilerShouldRevealAnswer() {
        var challenge = new Challenge29(scoreCard);

        Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler(passcode));
    }

    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new Challenge29(scoreCard);

        Assertions.assertThat(challenge.solved(passcode)).isTrue();
        Mockito.verify(scoreCard).completeChallenge(challenge);
    }

    @Test
    void incorrectAnswerShouldNotSolveChallenge() {
        var challenge = new Challenge29(scoreCard);

        Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
        Mockito.verifyNoInteractions(scoreCard);
    }

}
