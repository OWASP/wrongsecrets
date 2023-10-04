package org.owasp.wrongsecrets.challenges.kubernetes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

class Challenge33Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void defaultShouldNotDecrypt() {
    var challenge = new Challenge33(scoreCard, "if_you_see_this_please_use_k8s");
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    Assertions.assertThat(challenge.spoiler().solution())
        .isEqualTo("if_you_see_this_please_use_k8s");
  }

  @Test
  void decryptsCypherTextAndSolvesSolution() {
    var challenge =
        new Challenge33(
            scoreCard,
            "VBUGh3wu/3I1naHj1Uf97Y0Lq8B5/92q1jwp3/aYSwHSJI8WqdZnYLj78hESlfPPKf1ZKPap4z2+r+G9NRwdFU/YBMTY3cNguMm5C6l2pTK9JhPFnUzerIwMrnhu9GjrqSFn/BtOvLnQa/mSgXDNJYUOU8gCHFs9JEeQv9hpWpyxlB2Nqu0MHrPNODY3ZohhkjWXaxbjCZi9SpmHydU06Z7LqWyF39G6V8CF6LBPkdUn3aJAV++F0Q9IcSM=");
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    Assertions.assertThat(challenge.spoiler().solution())
        .isNotEqualTo("if_you_see_this_please_use_k8s");
  }
}
