package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge33Test {

  @Test
  void defaultShouldNotDecrypt() {
    var challenge = new Challenge33("if_you_see_this_please_use_k8s");
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    assertThat(challenge.spoiler().solution()).isEqualTo("if_you_see_this_please_use_k8s");
  }

  @Test
  void decryptsCypherTextAndSolvesSolution() {
    var challenge =
        new Challenge33(
            "VBUGh3wu/3I1naHj1Uf97Y0Lq8B5/92q1jwp3/aYSwHSJI8WqdZnYLj78hESlfPPKf1ZKPap4z2+r+G9NRwdFU/YBMTY3cNguMm5C6l2pTK9JhPFnUzerIwMrnhu9GjrqSFn/BtOvLnQa/mSgXDNJYUOU8gCHFs9JEeQv9hpWpyxlB2Nqu0MHrPNODY3ZohhkjWXaxbjCZi9SpmHydU06Z7LqWyF39G6V8CF6LBPkdUn3aJAV++F0Q9IcSM=");
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    assertThat(challenge.spoiler().solution()).isNotEqualTo("if_you_see_this_please_use_k8s");
  }
}
