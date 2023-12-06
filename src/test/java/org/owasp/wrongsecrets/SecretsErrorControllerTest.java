package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecretsErrorControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  void shouldReturnErrorPage() throws Exception {
    mvc.perform(get("/error"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("WHOOOOPS")))
        .andExpect(content().string(containsString("Something went wrong!")));
  }
}
