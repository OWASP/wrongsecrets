package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChallengeBuildContainerTest {

    private ChallengeBuildContainer challenge;
    private final String expectedSecret = "WSECR-build-layer-secret-849201";

    @BeforeEach
    void setUp() {
        challenge = new ChallengeBuildContainer(expectedSecret);
    }

    @Test
    void shouldAcceptCorrectBuildSecret() {
        assertThat(challenge.answerCorrect(expectedSecret)).isTrue();
    }

    @Test
    void shouldRejectIncorrectSecret() {
        assertThat(challenge.answerCorrect("wrong-secret")).isFalse();
    }

    @Test
    void shouldProvideCorrectSpoiler() {
        assertThat(challenge.spoiler().solution()).isEqualTo(expectedSecret);
    }
}
