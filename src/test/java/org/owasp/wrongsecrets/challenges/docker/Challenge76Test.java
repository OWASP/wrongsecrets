package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class Challenge76Test {

  @Autowired private Challenges challenges;

  @Autowired private MockMvc mvc;

  @Test
  void spoilerShouldReturnTheIndexedDevelopmentSecret() {
    var challenge = new Challenge76();

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler(Challenge76.DEVELOPMENT_SECRET));
  }

  @Test
  void answerCorrectShouldReturnTrueForCorrectAnswer() {
    var challenge = new Challenge76();

    assertThat(challenge.answerCorrect(Challenge76.DEVELOPMENT_SECRET)).isTrue();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void answerCorrectShouldReturnFalseForIncorrectAnswer() {
    var challenge = new Challenge76();

    assertThat(challenge.answerCorrect("wronganswer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
    assertThat(challenge.answerCorrect("vector-store-dev-secret")).isFalse();
  }

  @Test
  void searchShouldReturnTheChunkThatContainsTheSecret() {
    var challenge = new Challenge76();

    var results = challenge.search("credential");

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().text()).contains(Challenge76.DEVELOPMENT_SECRET);
  }

  @Test
  void searchWithoutQueryShouldReturnAllIndexedChunks() {
    var challenge = new Challenge76();

    assertThat(challenge.search(null)).hasSize(4);
    assertThat(challenge.search("  ")).hasSize(4);
  }

  @Test
  void searchShouldMatchCaseInsensitivelyAndReturnEmptyForUnknownQueries() {
    var challenge = new Challenge76();

    assertThat(challenge.search("RECALL")).hasSize(1);
    assertThat(challenge.search("RECALL").getFirst().text()).contains("0.78");
    assertThat(challenge.search("does-not-exist")).isEmpty();
  }

  @Test
  void searchEndpointShouldBeUnauthenticatedAndLeakTheSecret() throws Exception {
    mvc.perform(get("/rag/search"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Challenge76.DEVELOPMENT_SECRET)));
  }

  @Test
  void searchEndpointShouldFilterOnTheQueryParameter() throws Exception {
    mvc.perform(get("/rag/search").param("q", "recall"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("0.78")))
        .andExpect(content().string(not(containsString(Challenge76.DEVELOPMENT_SECRET))));
  }

  @Test
  void challenge76ShouldBeRegisteredAndSolveable() {
    var definition = challenges.findByShortName("challenge-76");

    assertThat(definition).isPresent();
    assertThat(challenges.getChallenge(definition.get())).hasSize(1);
    var challenge = challenges.getChallenge(definition.get()).getFirst();

    assertThat(challenge).isInstanceOf(Challenge76.class);
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
