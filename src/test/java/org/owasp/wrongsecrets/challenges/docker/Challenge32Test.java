package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public class Challenge32Test {
    @Mock
    private ScoreCard scoreCard;


    @Test
    void spoilerShouldGiveAnswer() {
        var challenge = new Challenge32(scoreCard);
        Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
        Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    }

    @Test
    void incorrectAnswerShouldNotSolveChallenge() {
        var challenge = new Challenge29(scoreCard);
        Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
    }

}
