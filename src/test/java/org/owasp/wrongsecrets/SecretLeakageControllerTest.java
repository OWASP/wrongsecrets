package org.owasp.wrongsecrets;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.wrongsecrets.challenges.docker.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class SecretLeakageControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @MockBean
    VaultTemplate vaultTemplate;
    @MockBean
    RuntimeEnvironment runtimeEnvironment;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        when(runtimeEnvironment.getRuntimeEnvironment()).thenReturn(RuntimeEnvironment.Environment.DOCKER);
    }

    @Test
    void spoil4() throws Exception {
        testSpoil("/spoil-4", Constants.password);
    }

    @Test
    void solveChallenge4() throws Exception {
        solveChallenge("/challenge/4", Constants.password);
    }

    private void solveChallenge(String endpoint, String solution) throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                        .param("solution", solution)
                        .param("action", "submit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Your answer is correct!")));
    }

    private void testSpoil(String endpoint, String solution) throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString(solution)));
    }

}
