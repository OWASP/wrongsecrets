package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.testutil.MockMvcTestSupport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecretsErrorControllerTest extends MockMvcTestSupport {

  @Test
  void shouldReturnErrorPage() throws Exception {
    mvc.perform(get("/error"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("WHOOOOPS")))
        .andExpect(content().string(containsString("Something went wrong!")));
  }
}
