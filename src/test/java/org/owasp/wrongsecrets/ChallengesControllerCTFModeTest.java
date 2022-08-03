package org.owasp.wrongsecrets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.wrongsecrets.challenges.ChallengeForm;
import org.owasp.wrongsecrets.challenges.docker.Challenge1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
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
        var spoil = new Challenge1(new InMemoryScoreCard(1)).spoiler().solution();
        mvc.perform(post("/challenge/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("ba9a72ac7057576344856")));

    }
}
