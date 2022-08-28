package org.owasp.wrongsecrets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.RuntimeEnvironment.Environment;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeUI;
import org.owasp.wrongsecrets.challenges.ChallengesController;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChallengesControllerTest {

    private MockMvc mvc;
    @Mock
    private Challenge challenge;
    @Mock
    private ScoreCard scoreCard;

    @BeforeEach
    void setup() {
        var env = new RuntimeEnvironment(Environment.GCP);
        var controller = new ChallengesController(scoreCard, ChallengeUI.toUI(List.of(challenge), env), env);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void startingChallengeShouldClearCorrectOrIncorrectMessage() throws Exception {
        when(challenge.solved(anyString())).thenReturn(false);

        this.mvc.perform(post("/challenge/1")
                .param("solution", "wrong")
                .param("action", "submit"))
            .andExpect(status().isOk())
            .andExpect(model().attributeDoesNotExist("answerCorrect"))
            .andExpect(model().attributeExists("answerIncorrect"));
        this.mvc.perform(get("/challenge/1"));
    }

    @Test
    void shouldReturnSpoiler() throws Exception {
        when(challenge.spoiler()).thenReturn(new Spoiler("solution"));
        this.mvc.perform(get("/spoil-1"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("spoiler", new Spoiler("solution")));
    }

    @Test
    void shouldReturnIncorrectAnswerMessageWhenChallengeIsNotSolved() throws Exception {
        when(challenge.solved(anyString())).thenReturn(false);

        this.mvc.perform(post("/challenge/1")
                .param("solution", "wrong")
                .param("action", "submit"))
            .andExpect(status().isOk())
            .andExpect(model().attributeDoesNotExist("answerCorrect"))
            .andExpect(model().attributeExists("answerIncorrect"));
    }

    @Test
    void shouldReturnCorrectAnswerMessageWhenChallengeIsSolved() throws Exception {
        when(challenge.solved(anyString())).thenReturn(true);

        this.mvc.perform(post("/challenge/1")
                .param("solution", "wrong")
                .param("action", "submit"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("answerCorrect"))
            .andExpect(model().attributeDoesNotExist("answerIncorrect"));
    }

    @Test
    void shouldReturnCompleteWhenAllItemsDone() throws Exception {
        when(challenge.solved(anyString())).thenReturn(true);
        this.mvc.perform(post("/challenge/1")
                .param("solution", "wrong")
                .param("action", "submit"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("answerCorrect"))
            .andExpect(model().attributeExists("allCompleted"));
    }

}
