package org.owasp.wrongsecrets.challenges.cloud;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@ExtendWith(MockitoExtension.class)
class Challenge10Test {

    @Mock
    private ScoreCard scoreCard;

    @Test
    void solveChallenge10WithAWSFile(@TempDir Path dir) throws Exception {
        var testFile = new File(dir.toFile(), "wrongsecret-2");
        var secret = "secretvalueWitFile";
        Files.writeString(testFile.toPath(), secret, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        var challenge = new Challenge10(scoreCard, dir.toString(), "test");

        Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
    }

    @Test
    void solveChallenge10WithoutAWSFile(@TempDir Path dir) throws Exception {
        var challenge = new Challenge10(scoreCard, dir.toString(), "test");

        Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
    }

}