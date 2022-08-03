package org.owasp.wrongsecrets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.RuntimeEnvironment.Environment;
import org.owasp.wrongsecrets.challenges.*;
import org.owasp.wrongsecrets.challenges.docker.Challenge1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientResponseException;

import javax.ws.rs.core.Application;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
    properties = {"CTF_ENABLED=true", "CTF_KEY=randomtextforkey"},
    classes = WrongSecretsApplication.class
)
@AutoConfigureMockMvc
class ChallengesControllerCTFModeTest {

    @Autowired
    private MockMvc mvc;


    @Test
    void shouldNotSpoilWhenInCTFMode() throws Exception {
        mvc.perform(get("/spoil-1"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));

    }

    @Test
    void shouldShowFlagWhenRespondingWithSuccessInCTFMode() throws Exception {
        var spoil = new ChallengeForm(new Challenge1(new InMemoryScoreCard(1)).spoiler().solution()).toString();
        mvc.perform(post("/challenges/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .param("csrf","fd6aae2e-e85b-4c52-96f3-d71c6c725d11"))
            .andExpect(status().isOk());

    }
}
