package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.testutil.MockMvcTestSupport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AboutControllerTests extends MockMvcTestSupport {

  @Test
  void shouldGetAbout() throws Exception {
    mvc.perform(get("/about"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("About")));
  }
}
