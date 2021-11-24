package org.owasp.wrongsecrets;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

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
    @Value("${password}")
    private String hardcodedPassword;
    @Value("${ARG_BASED_PASSWORD}")
    private String argBasedPassword;
    @Value("${DOCKER_ENV_PASSWORD}")
    String hardcodedEnvPassword;

    @Value("${default_aws_value}")
    String tempAWSfiller;

    @Value("${secretmountpath}")
    String tempMountPath;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void spoil1() throws Exception {
        testSpoil("/spoil-1", hardcodedPassword);
    }

    @Test
    void spoil2() throws Exception {
        testSpoil("/spoil-2", argBasedPassword);
    }

    @Test
    void spoil3() throws Exception {
        testSpoil("/spoil-3", hardcodedEnvPassword);
    }

    @Test
    void spoil4() throws Exception {
        testSpoil("/spoil-4", Constants.password);
    }

    @Test
    void solveChallenge1() throws Exception {
        solveChallenge("/challenge/1", hardcodedPassword);
    }

    @Test
    void solveChallenge2() throws Exception {
        solveChallenge("/challenge/2", argBasedPassword);
    }

    @Test
    void solveChallenge3() throws Exception {
        solveChallenge("/challenge/3", hardcodedEnvPassword);
    }

    @Test
    void solveChallenge4() throws Exception {
        solveChallenge("/challenge/4", Constants.password);
    }

    @Test
    void solveChallenge9WithoutFile() throws Exception {
        solveChallenge("/challenge/9", tempAWSfiller);
    }

    @Test
    void solveChallenge9WithFile() throws Exception {
        File testFile = new File(tempMountPath, "wrongsecret");
        String secret = "secretvalueWitFile";
        Files.writeString(testFile.toPath(), secret, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        solveChallenge("/challenge/9", secret);
        testFile.deleteOnExit();
    }


    private void solveChallenge(String endpoint, String solution) throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(endpoint).param("solution", solution))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("Your answer is correct!")));
    }

    private void testSpoil(String endpoint, String soluton) throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString(soluton)));
    }

}
